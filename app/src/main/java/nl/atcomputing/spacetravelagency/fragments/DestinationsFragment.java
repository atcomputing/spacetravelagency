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

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.activities.ImageGalleryActivity;
import nl.atcomputing.spacetravelagency.adapters.DestinationsAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class DestinationsFragment extends SherlockFragment implements OnItemClickListener {

	private DestinationsFragmentInterface listener;
	private DestinationsAdapter adapter;
	private View view;
	
	public interface DestinationsFragmentInterface {
		public void onDestinationClicked(int pos);
	}

	public void setAdapter(DestinationsAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.listener = (DestinationsFragmentInterface) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnDestinationClickedListener");
		}
		setHasOptionsMenu(true);
	}

	public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
		this.listener.onDestinationClicked(pos);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance) {
		this.view = inflater.inflate(R.layout.destinations_fragment, container, false);
		return this.view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if( adapter == null ) {
			Log.e("DestinationsFragment", "Use DestinationsFragment.setAdapter(ImageWithTextAndCheckboxAdapter adapter) to set the adapter for the listview");
			return;
		}

		ListView lv = (ListView) this.view.findViewById(R.id.destinations_listview);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.destinationsfragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId()) {
		case R.id.menu_camera:
			intent = new Intent(getActivity(), ImageGalleryActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
