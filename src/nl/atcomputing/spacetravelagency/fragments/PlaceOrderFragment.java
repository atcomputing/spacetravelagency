package nl.atcomputing.spacetravelagency.fragments;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.StateSingleton;
import nl.atcomputing.spacetravelagency.app.SoundSherlockFragmentActivity;
import nl.atcomputing.spacetravelagency.database.Database;
import nl.atcomputing.spacetravelagency.order.HttpCommunicator;
import nl.atcomputing.spacetravelagency.order.Order;
import nl.atcomputing.spacetravelagency.order.Order.OrderListener;
import nl.atcomputing.spacetravelagency.utils.Sounds;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceOrderFragment extends SherlockFragment implements OrderListener {

	private BroadcastReceiver broadcastReceiver;

	private final String BUNDLE_KEY_ZOOM = "zoom";
	private final String BUNDLE_KEY_POSITION_LAT = "position_lat";
	private final String BUNDLE_KEY_POSITION_LNG = "position_lng";

	private boolean mapInitialized;

	private float zoom = 12;
	private LatLng cameraPosition = new LatLng(52.07895,5.09789);

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.placeorder_fragment, container, false);
		return this.view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Activity activity = getActivity();

		StateSingleton state = StateSingleton.getInstance();
		Order order = state.getOrder();
		if( HttpCommunicator.isOnline(activity) ) {
			if( order == null ) {
				order = createOrder();
				state.setOrder(order);
				placeOrder();
				hideMapFragment();
			} else if( ! order.isComplete() ){
				state.setOrder(order);
				placeOrder();
				hideMapFragment();
			}
		}

		if( savedInstanceState != null ) {
			this.zoom = savedInstanceState.getFloat(BUNDLE_KEY_ZOOM);
			this.cameraPosition = new LatLng(savedInstanceState.getDouble(BUNDLE_KEY_POSITION_LAT), 
					savedInstanceState.getDouble(BUNDLE_KEY_POSITION_LNG));
		}

		try {
			MapsInitializer.initialize(activity);
			this.mapInitialized = true;
		} catch (GooglePlayServicesNotAvailableException e) {
			this.mapInitialized = false;
		}

		updateView();
	}

	@Override
	public void onResume() {
		super.onResume();

		this.broadcastReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				/**
				 * onReceive is called when network connectivity changes
				 * but also when we register the BroadcastReceiver
				 */
				StateSingleton state = StateSingleton.getInstance();
				if( StateSingleton.getInstance().getOrder() == null ) {
					if( HttpCommunicator.isOnline(getActivity()) ) {
						Order order = createOrder();
						state.setOrder(order);
						placeOrder();
					}
				}
			}
		};

		getActivity().registerReceiver(this.broadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(this.broadcastReceiver);
	}

	/**
	 * NOTE: setting a fragment retain instance to true will prevent
	 * the system from passing the outState bundle in onActivityCreated
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		GoogleMap map = getMap();
		if( map != null ) {
			outState.putFloat(BUNDLE_KEY_ZOOM, map.getCameraPosition().zoom);
			outState.putDouble(BUNDLE_KEY_POSITION_LAT, map.getCameraPosition().target.latitude);
			outState.putDouble(BUNDLE_KEY_POSITION_LNG, map.getCameraPosition().target.longitude);
		}

	}

	private void placeOrder() {
		Activity activity = getActivity();

		Order order = StateSingleton.getInstance().getOrder();

		if( HttpCommunicator.isOnline(activity) ) {
			if( (! order.isConfirmed() ) || ( ! order.isConfirming() )) {
				order.setConfirming();
				HttpCommunicator.placeOrder(activity, this, order);
			} else if( ( order.getDepartureLocation() == null ) || ( order.getDepartureTime() == 0 ) ) {
				HttpCommunicator.retrieveDepartureInformation(activity, this, order);
			}
		}
	}

	private Order createOrder() {
		Activity activity = getActivity();

		Order order = new Order();

		String android_id = Secure.getString(activity.getContentResolver(),
				Secure.ANDROID_ID);
		order.setOrderId(android_id);
		order.fillFromDatabase(activity);
		return order;
	}

	private void updateView() {
		Order order = StateSingleton.getInstance().getOrder();

		Activity activity = getActivity();
		if( activity == null ) {
			//Thread may return after activity has been recreated or destroyed.
			//This prevents CheckoutFragment from trying to update when activity
			//is no longer available
			return;
		}

		TextView tv = (TextView) this.view.findViewById(R.id.confirmed_text);
		if( order.isConfirmed() ) {
			tv.setText(R.string.order_confirmed);
		} else if( order.isConfirming() ) {
			if( HttpCommunicator.isOnline(getActivity()) ) {
				tv.setText(R.string.confirming_order_please_wait_);
			} else {
				tv.setText(R.string.enable_internet_to_book_journey);
			}
		} else if( order.isNotConfirmed() ){
			tv.setText(R.string.could_not_confirm_order_please_return_to_checkout_and_try_again_);
		} else {
			if( HttpCommunicator.isOnline(getActivity()) ) {
				tv.setText(R.string.could_not_confirm_order_please_return_to_checkout_and_try_again_);
			} else {
				tv.setText(R.string.enable_internet_to_retrieve_order_information);
			}
		}

		long departureTime = order.getDepartureTime();
		if( departureTime > 0 ) {
			setDepartureTime(departureTime);
		}

		String departureLocation = order.getDepartureLocation();
		if( departureLocation != null ) {
			LatLng latlng = convertDepartureLocation(departureLocation);
			setLocation(latlng, this.cameraPosition);
			showMapFragment();
		}
	}

	private void setDepartureTime(long departureTime) {
		CharSequence dateString = DateUtils.formatDateTime(getActivity(), 
				departureTime * 1000,
				DateUtils.FORMAT_SHOW_DATE | 
				DateUtils.FORMAT_SHOW_TIME);
		TextView tv = (TextView) this.view.findViewById(R.id.departure_time_value);
		tv.setText(dateString);

		View v = this.view.findViewById(R.id.linearlayout_departure_time);
		v.setVisibility(View.VISIBLE);
	}

	private LatLng convertDepartureLocation(String departureLocation) {
		String[] location = departureLocation.split(",");
		if( location.length == 2 ) {
			try{
				double latitude = Double.parseDouble(location[0]);
				double longitude = Double.parseDouble(location[1]);
				return new LatLng(latitude, longitude);
			} catch (NumberFormatException e) {
				Log.w("PlaceOrderActivity", "Could not parse departure location.\n"+
						e.getMessage());
			}
		}
		return null;
	}

	private void showMapFragment() {
		FragmentManager fragmentManager = getFragmentManager();

		Fragment fragment = fragmentManager.findFragmentById(R.id.map);
		fragment.setRetainInstance(true);

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.show(fragment);
		fragmentTransaction.commit();
	}

	private void hideMapFragment() {
		FragmentManager fragmentManager = getFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.map);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.hide(fragment);
		fragmentTransaction.commit();
	}

	private void setLocation(LatLng marker, LatLng camera) {
		if( ! this.mapInitialized ) {
			return;
		}

		GoogleMap mMap = getMap();
		if( mMap == null ) {
			return;
		}

		MarkerOptions mo = new MarkerOptions();
		mo.position(marker);
		mo.title(getString(R.string.app_name));
		BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.spaceship_launcher);
		if( bd == null ) {
			return;
		}

		mo.icon(bd);
		mMap.addMarker(mo);

		CameraPosition cameraPosition = new CameraPosition(camera, this.zoom, 0, 0);
		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));	
	}

	private GoogleMap getMap() {
		FragmentManager fragmentManager = getFragmentManager();
		SupportMapFragment fragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
		if( fragment == null ) {
			return null;
		}

		return fragment.getMap();
	}

	@Override
	public void onOrderProcessed(Order order) {
		StateSingleton.getInstance().setOrder(order);

		SoundSherlockFragmentActivity activity = (SoundSherlockFragmentActivity) getActivity();
		if( activity == null ) {
			return;
		}

		Sounds sounds = activity.getSounds();
		if( order.isConfirmed() ) {
			sounds.play(Sounds.Type.DOCK);

			HttpCommunicator.retrieveDepartureInformation(activity, this, order);

			Database db = new Database(activity, "");
			db.open();
			String orderId = order.getOrderId();
			long departureTime = order.getDepartureTime();
			String departureLocation = order.getDepartureLocation();
			Cursor cursor = db.getOrder(orderId);
			if( cursor.moveToFirst() ) {
				//Prevent adding multiple identical orders in database
				if( db.orderChanged(orderId, departureTime, departureLocation)) {
					db.addOrder(orderId, departureTime, departureLocation);
				}
			} else {
				db.addOrder(orderId, departureTime, departureLocation);
			}
			db.close();
		} else if( order.isNotConfirmed() ) {
			sounds.play(Sounds.Type.FAIL);
		}
		updateView();
	}

	@Override
	public void onDepartureInformationReceived(Order order) {
		StateSingleton.getInstance().setOrder(order);
		updateView();
	}
}
