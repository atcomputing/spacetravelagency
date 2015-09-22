/**
 * 
 * Copyright 2015 AT Computing BV
 *
 * This file is part of Space Travel Agency.
 *
 * Space Travel Agency is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Space Travel Agency is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Space Travel Agency.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package nl.atcomputing.spacetravelagency.fragments;
import java.util.ArrayList;

import nl.atcomputing.spacetravelagency.Planet;
import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.StateSingleton;
import nl.atcomputing.spacetravelagency.database.Database;
import nl.atcomputing.spacetravelagency.database.DatabaseHelper;
import nl.atcomputing.spacetravelagency.utils.CalculateTravellingOrder;
import nl.atcomputing.spacetravelagency.utils.CalculateTravellingOrder.CalculateTravellingOrderListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class CheckoutFragment extends SherlockFragment implements CalculateTravellingOrderListener {

	//	private static final String KEY_TRAVELLING_ORDER = "travellingOrder";
	//	private static final String KEY_INVITED_FRIEND = "invitedFriend";
	//	private static final String KEY_TOTAL_PRICE = "totalPrice";

	public static final String TAG = "checkout_fragment_tag";

	private View view;
	private CalculateTravellingOrder calculateTravellingOrderTask;
	private boolean onActivityCreatedCalled;

	/**
	 * Retrieves a previously created CheckoutFragment, or creates a new CheckoutFragment if not available, and replaces the fragment
	 * in the layout using resourceId and fragmentManager.
	 * @param resourceId resource identifier fragment should be added to
	 * @param fragmentManager
	 */
	static public void replace(int resourceId, FragmentManager fragmentManager) {
		CheckoutFragment checkoutFragment = (CheckoutFragment) fragmentManager.findFragmentByTag(TAG);

		if (checkoutFragment == null) { // no previous fragment available so we create a new one
			checkoutFragment = new CheckoutFragment();
		} 

		if(! checkoutFragment.isAdded()) { //prevent adding fragment multiple times
			fragmentManager.beginTransaction().replace(resourceId, checkoutFragment, TAG).commit();
		}
	}

	/**
	 * Retrieves a previously created CheckoutFragment, or creates a new CheckoutFragment if not available, and replaces the fragment
	 * in the layout using resourceId and fragmentManager.
	 * @param resourceId resource identifier fragment should be added to
	 * @param fragmentManager
	 */
	static public void add(int resourceId, FragmentManager fragmentManager) {
		fragmentManager.executePendingTransactions();

		CheckoutFragment checkoutFragment = (CheckoutFragment) fragmentManager.findFragmentByTag(TAG);

		if (checkoutFragment == null) { // no previous fragment available so we create a new one
			checkoutFragment = new CheckoutFragment();
		} 

		if(! checkoutFragment.isAdded()) { //prevent adding fragment multiple times
			fragmentManager.beginTransaction().add(resourceId, checkoutFragment, TAG).commit();
		}
	}

	static public void remove(FragmentManager fragmentManager) {
		CheckoutFragment checkoutFragment = (CheckoutFragment) fragmentManager.findFragmentByTag(TAG);
		if( checkoutFragment != null ) {
			fragmentManager.beginTransaction().remove(checkoutFragment).commit();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		this.onActivityCreatedCalled = false;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance) {
		this.view = inflater.inflate(R.layout.checkout_fragment, container, false);
		return this.view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		this.onActivityCreatedCalled = true;

		Context context = getActivity();

		StateSingleton state = StateSingleton.getInstance();
		state.setCheckingOut();

		Planet[] travellingOrder = state.getTravellingOrder();
		if( travellingOrder == null ) {
			Database db = new Database(context, "");
			db.open();
			Cursor cursor = db.getAllDestinations();
			int indexStarsystem = cursor.getColumnIndex(DatabaseHelper.Destinations.STAR_SYSTEM);
			int indexPrice = cursor.getColumnIndex(DatabaseHelper.Destinations.PRICE);
			int indexDestination = cursor.getColumnIndex(DatabaseHelper.Destinations.DESTINATION);
			int indexDistance = cursor.getColumnIndex(DatabaseHelper.Destinations.DISTANCE);
			ArrayList<Planet> destinations = new ArrayList<Planet>();
			int totalPrice = 0;
			while (cursor.moveToNext()) {
				totalPrice += cursor.getLong(indexPrice);
				Planet p = new Planet(cursor.getString(indexDestination), 
						cursor.getInt(indexDistance), 
						cursor.getInt(indexPrice), 
						cursor.getString(indexStarsystem), 
						"");
				destinations.add(p);
			}
			state.setTotalPrice(totalPrice);
			cursor.close();
			db.close();

			this.calculateTravellingOrderTask = state.getCalculateTravellingOrderTask();
			if( this.calculateTravellingOrderTask == null ) {
				this.calculateTravellingOrderTask = new CalculateTravellingOrder(context, this);
				state.setCalculateTravellingOrderTask(this.calculateTravellingOrderTask);
				this.calculateTravellingOrderTask.execute(destinations.toArray(new Planet[0]));
			} else {
				this.calculateTravellingOrderTask.setListener(this);
				this.calculateTravellingOrderTask.playSound();
			}
		} else {
			showTravellingOrder(travellingOrder);
		}

		String invitedFriend = state.getInvitedFriend();
		if( invitedFriend != null ) {
			showInvitedFriend(invitedFriend);
		}

		TextView tv = (TextView) this.view.findViewById(R.id.textview_total_price_value);
		tv.setText(Long.toString(state.getTotalPrice()));

	}

	@Override
	public void onStart() {
		super.onStart();
		if( ! this.onActivityCreatedCalled ) {
			if( this.calculateTravellingOrderTask != null ) {
				this.calculateTravellingOrderTask.playSound();
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if( this.calculateTravellingOrderTask != null ) {
			this.calculateTravellingOrderTask.stopSound();
		}
		this.onActivityCreatedCalled = false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.checkoutfragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId()) {
		case R.id.menu_invite_a_friend:
			intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, 1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (1) :
			if (resultCode == Activity.RESULT_OK) {
				Uri contactUri = data.getData();
				String[] projection = {ContactsContract.Contacts.DISPLAY_NAME};
				Cursor c = getActivity().getContentResolver().query(contactUri, projection, null, null, null);
				if (c.moveToFirst()) {
					String invitedFriend = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					StateSingleton.getInstance().setInvitedFriend(invitedFriend);
					showInvitedFriend(invitedFriend);
				}
			}
		break;
		}
	}

	@Override
	public void handleTravellingOrder(Planet[] result) {
		StateSingleton.getInstance().setTravellingOrder(result);
		showTravellingOrder(result);
		this.calculateTravellingOrderTask = null;
		StateSingleton.getInstance().setCalculateTravellingOrderTask(this.calculateTravellingOrderTask);
	}

	private void showInvitedFriend(String invitedFriend) {
		TextView tv = (TextView) this.view.findViewById(R.id.textview_invited_friend);
		tv.setText(getString(R.string.invited_friend_) + invitedFriend);
		tv.setVisibility(View.VISIBLE);
	}

	private void showTravellingOrder(Planet[] travellingOrder) {
		Activity activity = getActivity();
		if( activity == null ) {
			//Thread may return after activity has been recreated or destroyed.
			//This prevents CheckoutFragment from trying to update when activity
			//is no longer available
			return;
		}
		
		ArrayList<String> planetNames = new ArrayList<String>();
		
		for( Planet p : travellingOrder ) {
			planetNames.add(p.getName());
		}
		
		TextView tv = (TextView) this.view.findViewById(R.id.textview_calculating_travelling_order);
		tv.setText(R.string.travelling_order_);
		ArrayAdapter<String> ad = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, planetNames.toArray(new String[0]));
		ListView lv = (ListView) this.view.findViewById(R.id.listview);
		lv.setAdapter(ad);
	}
}
