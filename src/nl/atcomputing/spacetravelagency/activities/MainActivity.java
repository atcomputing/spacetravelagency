package nl.atcomputing.spacetravelagency.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.StateSingleton;
import nl.atcomputing.spacetravelagency.adapters.DestinationsAdapter;
import nl.atcomputing.spacetravelagency.adapters.DestinationsAdapter.DestinationsAdapterListener;
import nl.atcomputing.spacetravelagency.app.SoundSherlockFragmentActivity;
import nl.atcomputing.spacetravelagency.database.Database;
import nl.atcomputing.spacetravelagency.fragments.CheckoutFragment;
import nl.atcomputing.spacetravelagency.fragments.DestinationsFragment;
import nl.atcomputing.spacetravelagency.fragments.DestinationsFragment.DestinationsFragmentInterface;
import nl.atcomputing.spacetravelagency.fragments.PlanetInfoFragment;
import nl.atcomputing.spacetravelagency.fragments.PlanetInfoFragment.OnScrollableTextViewFragmentListener;
import nl.atcomputing.spacetravelagency.order.DepartureInfoService;
import nl.atcomputing.spacetravelagency.order.HttpCommunicator;
import nl.atcomputing.spacetravelagency.utils.Preferences;
import nl.atcomputing.spacetravelagency.utils.RotateImage;
import nl.atcomputing.spacetravelagency.utils.Sounds;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;

public class MainActivity extends SoundSherlockFragmentActivity 
implements OnClickListener, DestinationsFragmentInterface, DestinationsAdapterListener, TabListener,
SensorEventListener, OnScrollableTextViewFragmentListener {

	private final String BUNDLE_KEY_SELECTEDTAB = "BUNDLE_KEY_SELECTEDTAB";
	private final String BUNDLE_KEY_PLANETINFO = "BUNDLE_KEY_PLANETINFO";
	private final String TAG_PORTRAIT = "activity_main_port";
	private final String TAG_LANDSCAPE = "activity_main_land";

	private String layoutTag = "";

	private DestinationsAdapter sunPlanetsAdapter;
	private DestinationsAdapter siriusPlanetsAdapter;
	private String[] planetInfos;
	private String planetInfo;

	private String STAR_SYSTEM_SUN = "sun";
	private String STAR_SYSTEM_SIRIUS = "sirius"; 
	private String selectedTabName;

	private SensorManager sensorManager;
	private Sensor sensor;

	private Bitmap backgroundBitmapImage;
	private ImageView backgroundImageView;

	private BroadcastReceiver broadcastReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setupDatastructures();

		if( savedInstanceState != null ) {
			this.planetInfo = savedInstanceState.getString(BUNDLE_KEY_PLANETINFO);
		}

		this.layoutTag = (String) findViewById(R.id.main_layout).getTag();
		if( this.layoutTag == null ) {
			this.layoutTag = "";
		}
		setupFragments();

		addTabs();
		if( savedInstanceState != null ) {
			this.selectedTabName = savedInstanceState.getString(BUNDLE_KEY_SELECTEDTAB);
			selectTab(this.selectedTabName);
		}

		DepartureInfoService.cancelDepartureInfoServiceAlarm(this);
		DepartureInfoService.setupDepartureInfoServiceAlarm(this);

		Button b = (Button) findViewById(R.id.button);
		b.setOnClickListener(this);

		this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		this.sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

		if( this.layoutTag.contentEquals(TAG_PORTRAIT) ) {
			/**
			 * In portrait mode we show planet info if state is showing planet info
			 * or checkout if state is checking out.
			 * We assume that user rotated device to read the information fullscreen
			 */
			if( StateSingleton.getInstance().isShowingPlanetInfo() ) {
				showPlanetInfo();
			} else if( StateSingleton.getInstance().isCheckingOut() ) {
				showCheckout();
			}
		} else {
			//In landscape mode we always show destinations list
			//in onResume we determine what to show next to the destinations list
			showDestinations();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if( Preferences.animateBackground() ) {
			this.backgroundImageView = (ImageView) findViewById(R.id.background_image);
			this.backgroundBitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.milky_way);
			this.sensorManager.registerListener(this, this.sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}

		StateSingleton state = StateSingleton.getInstance();
		Log.d("MainActivity", "onResume: state="+state);
		if( this.layoutTag.contentEquals(TAG_LANDSCAPE) ) {
			if( state.isCheckingOut() || state.isPlacingOrder() ) {
				showCheckout();
			} else if ( state.isShowingPlanetInfo() ){
				showPlanetInfo();
			} else {
				state.setSelectingDestinations();
			}
		}

		this.broadcastReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				updateButton();
			}
		};
		registerReceiver(this.broadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

		updateButton();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if( this.backgroundBitmapImage != null ) {
			this.backgroundBitmapImage.recycle();
		}
		this.sensorManager.unregisterListener(this);

		unregisterReceiver(this.broadcastReceiver);
	}


	/**
	 * We need to override onBackPressed to make sure
	 * state is set to initial state when quitting the
	 * App. When returning from checkout to the main activity
	 * state is remembered (i.e. CHECKOUT). When user pressed
	 * back again to quit the App the StateSingleton is retained.
	 * This results in the App starting in Checkout mode when
	 * user restarts the App from the launcher
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		StateSingleton.getInstance().setSelectingDestinations();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(BUNDLE_KEY_SELECTEDTAB, this.selectedTabName);
		outState.putString(BUNDLE_KEY_PLANETINFO, this.planetInfo);
	}

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button:
			if( layoutTag.contentEquals(TAG_LANDSCAPE)) {
				if( StateSingleton.getInstance().isCheckingOut() ) {
					StateSingleton.getInstance().setPlacingOrder();
					showPlaceOrder();
				} else {
					showCheckout();
					StateSingleton.getInstance().setCheckingOut();
					updateButton();
				}
			} else {
				showCheckout();
			}
			break;
		}
	}

	@Override
	public void onDestinationClicked(int pos) {
		StateSingleton.getInstance().setShowingPlanetInfo();
		this.planetInfo = this.planetInfos[pos];
		showPlanetInfo();
		updateButton();
	}

	@Override
	public void onCheckBoxStateChanged(boolean isChecked, int planetId) {
		StateSingleton.getInstance().setSelectingDestinations();
		
		int[] prices;
		int[] distances;
		String[] names;
		Database db;

		if( this.selectedTabName.contains(STAR_SYSTEM_SUN) ) {
			db = new Database(this, STAR_SYSTEM_SUN);
			prices = getResources().getIntArray(R.array.sun_planets_price);
			names = getResources().getStringArray(R.array.sun_planets_names);
			distances = getResources().getIntArray(R.array.sun_planets_distance_from_center);
		} else {
			db = new Database(this, STAR_SYSTEM_SIRIUS);
			prices = getResources().getIntArray(R.array.sirius_planets_price);
			names = getResources().getStringArray(R.array.sirius_planets_names);
			distances = getResources().getIntArray(R.array.sirius_planets_distance_from_center);
		}

		db.open();
		if (isChecked) {
			db.addDestination(planetId, names[planetId], prices[planetId], distances[planetId]);
		} else {
			db.removeDestination(planetId);
		}
		db.close();

		//Any previous checkout is invalid now
		StateSingleton.getInstance().setTravellingOrder(null);
		StateSingleton.getInstance().setOrder(null);
		StateSingleton.getInstance().setInvitedFriend(null);
		StateSingleton.getInstance().setCalculateTravellingOrderTask(null);
		CheckoutFragment.remove(getSupportFragmentManager());

		updateButton();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if( this.selectedTabName != ((String) tab.getTag()) ) {
			this.selectedTabName = (String) tab.getTag();
			//			this.planetInfo = ""; // clear planet info if different galaxy is selected

			super.sounds.play(Sounds.Type.ITEMPRESS);
		}
		showDestinations();
		updateButton();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		//		onTabSelected(tab, ft);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if( this.backgroundImageView != null ) {
			Bitmap bmRotated = RotateImage.rotate(this.backgroundBitmapImage, 
					-event.values[0], event.values[1]);
			this.backgroundImageView.setImageBitmap(bmRotated);
		}
	}

	private DestinationsAdapter createAdapter(String starSystem, String[] planetNames, TypedArray planetIcons) {
		ArrayList<Map<String, Object>> planetMap = new ArrayList<Map<String, Object>>();

		for(int i = 0; i < planetNames.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("planetName", planetNames[i]);
			map.put("planetImage", Integer.valueOf(planetIcons.getResourceId(i, 0)));
			map.put("planetId", i);
			planetMap.add(map);
		}

		String[] fromMapKeys = new String[] { "planetImage", "planetName" };
		int[] toTargetResourceIds = new int[] {R.id.destinations_listview_item_planet_image,
				R.id.destinations_listview_item_planet_name};

		Database db = new Database(this, starSystem);
		return new DestinationsAdapter(this, this, db, 
				planetMap, R.layout.destinations_listview_item, fromMapKeys, toTargetResourceIds);
	}

	private void setupFragments() {
		StateSingleton state = StateSingleton.getInstance();

		if( layoutTag.equals(TAG_LANDSCAPE) ) {
			if( state.isCheckingOut() ) {
				showCheckout();
			} else if ( state.isShowingPlanetInfo() ) {
				showPlanetInfo();
			}
		}
	}

	private void selectTab(String tabName) {
		ActionBar actionBar = getSupportActionBar();
		int amountOfTabs = actionBar.getTabCount();
		for( int i = 0; i < amountOfTabs; i++ ) {
			Tab tab = actionBar.getTabAt(i);
			if( ((String) tab.getTag()).contains(tabName) ) {
				tab.select();
				break;
			}
		}
	}

	private void addTabs() {		
		ActionBar actionBar = getSupportActionBar();
		if( actionBar.getTabCount() > 0 ) { // tabs already available no need to add them again
			return;
		}

		this.selectedTabName = STAR_SYSTEM_SUN;

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab sunTab = actionBar.newTab();
		sunTab.setText(R.string.Sun);
		sunTab.setTabListener(this);
		sunTab.setTag(STAR_SYSTEM_SUN);
		actionBar.addTab(sunTab);

		Tab siriusTab = actionBar.newTab();
		siriusTab.setText(R.string.Sirius);
		siriusTab.setTabListener(this);
		siriusTab.setTag(STAR_SYSTEM_SIRIUS);
		actionBar.addTab(siriusTab);
	}

	private void setupDatastructures() {
		String[] planetNames = getResources().getStringArray(R.array.sun_planets_names);
		TypedArray planetIcons = getResources().obtainTypedArray(R.array.sun_planets_icons);
		this.sunPlanetsAdapter = createAdapter(this.STAR_SYSTEM_SUN, planetNames, planetIcons);

		planetNames = getResources().getStringArray(R.array.sirius_planets_names);
		planetIcons = getResources().obtainTypedArray(R.array.sirius_planets_icons);
		this.siriusPlanetsAdapter = createAdapter(this.STAR_SYSTEM_SIRIUS, planetNames, planetIcons);
		planetIcons.recycle();

		this.planetInfos = getResources().getStringArray(R.array.sun_planets_info);
	}

	private void updateButton() {
		Button button = (Button) findViewById(R.id.button);
		int stringResourceId = R.string.checkout;
		int amountChecked = this.sunPlanetsAdapter.getAmountOfCheckboxesSelected() +
				this.siriusPlanetsAdapter.getAmountOfCheckboxesSelected();

		Log.d("MainActivity", "updateButton: StateSingleton="+StateSingleton.getInstance());
		
		StateSingleton state = StateSingleton.getInstance();
		if( this.layoutTag.contentEquals(TAG_LANDSCAPE) ) {
			if( state.isCheckingOut() || state.isPlacingOrder() ) {
				if( HttpCommunicator.isOnline(this) ) {
					stringResourceId = R.string.book_journey;
					button.setEnabled(true);
				} else {
					stringResourceId = R.string.enable_internet_to_book_journey;
					button.setEnabled(false);
				}
			} else {
				if( amountChecked > 0 ) {
					button.setEnabled(true);
				} else {
					stringResourceId = R.string.please_select_your_destination_s_;
					button.setEnabled(false);
				}
			}
		} else {
			if( amountChecked > 0 ) {
				button.setEnabled(true);
			} else {
				stringResourceId = R.string.please_select_your_destination_s_;
				button.setEnabled(false);
			}
		}
		
		button.setText(stringResourceId);
	}

	/**
	 * Creates and attaches the fragment to display planet information
	 * @param info text to display or null to show default text 
	 */
	private void showPlanetInfo() {
		PlanetInfoFragment scrollableTextViewFragment = new PlanetInfoFragment();

		Bundle bundle = new Bundle();
		bundle.putString(PlanetInfoFragment.ARG_KEY_TEXT, this.planetInfo);
		scrollableTextViewFragment.setArguments(bundle);
		scrollableTextViewFragment.setListener(this);

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		if( layoutTag.equals(TAG_PORTRAIT) ) {
			Intent intent = new Intent(this, PlanetInfoActivity.class);
			intent.putExtra(PlanetInfoActivity.BUNDLE_KEY_PLANETINFO, this.planetInfo);
			startActivity(intent);
		} else {
			fragmentTransaction.replace(R.id.framelayout_fragment_planet_info, scrollableTextViewFragment);
			fragmentTransaction.commit();
		}
	}

	/**
	 * Creates and attaches the fragment to display the list of destinations
	 * @param tab that has been selected
	 * @param ft used to replace the current fragment
	 */
	private void showDestinations() {
		DestinationsAdapter adapter;

		if( this.selectedTabName.contentEquals(STAR_SYSTEM_SUN) ) {
			adapter = this.sunPlanetsAdapter;
			this.planetInfos = getResources().getStringArray(R.array.sun_planets_info);
		} else {
			adapter = this.siriusPlanetsAdapter;
			this.planetInfos = getResources().getStringArray(R.array.sirius_planets_info);
		}

		DestinationsFragment destinationsFragment = new DestinationsFragment();

		destinationsFragment.setAdapter(adapter);

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		if( layoutTag.equals(TAG_PORTRAIT) ) {
			fragmentTransaction.replace(R.id.framelayout_fragment_container, destinationsFragment);
		} else {
			fragmentTransaction.replace(R.id.framelayout_fragment_destinations, destinationsFragment);
		}
		fragmentTransaction.commit();
	}

	private void showCheckout() {
		if( layoutTag.equals(TAG_PORTRAIT) ) {
			Intent intent = new Intent(this, CheckoutActivity.class);
			startActivity(intent);
		} else {
			CheckoutFragment.replace(R.id.framelayout_fragment_planet_info, getSupportFragmentManager());
		}

	}

	private void showPlaceOrder() {
		Intent intent = new Intent(this, PlaceOrderActivity.class);
		startActivity(intent);
	}

	@Override
	public void onTextViewClick() {
		Intent intent = new Intent(this, PlanetInfoActivity.class);
		intent.putExtra(PlanetInfoActivity.BUNDLE_KEY_PLANETINFO, this.planetInfo);
		startActivity(intent);
	}
}
