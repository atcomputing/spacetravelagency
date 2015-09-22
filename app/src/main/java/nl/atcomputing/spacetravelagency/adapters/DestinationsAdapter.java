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

package nl.atcomputing.spacetravelagency.adapters;
import java.util.List;
import java.util.Map;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.database.Database;
import nl.atcomputing.spacetravelagency.database.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DestinationsAdapter extends SimpleAdapter {

	private Context context;
	private DestinationsAdapterListener listener;
	private List<Map<String, Object>> planetMap;
	private SparseBooleanArray itemsChecked;
	
	
	public interface DestinationsAdapterListener {
		public void onCheckBoxStateChanged(boolean isChecked, int planetId);
	}
	
	public DestinationsAdapter(Context context,
			DestinationsAdapterListener listener,
			Database database,
			List<Map<String, Object>> planetMap, 
			int resource, String[] from,
			int[] to) {
		super(context, planetMap, resource, from, to);
		
		this.listener = listener;
		this.context = context;
		this.planetMap = planetMap;
		this.itemsChecked = new SparseBooleanArray();
		
		database.open();
		Cursor cursor = database.getDestinations();
		int indexPlanetId = cursor.getColumnIndex(DatabaseHelper.Destinations.DESTINATION_ID);
		while(cursor.moveToNext()) {
			int planetId = cursor.getInt(indexPlanetId);
			this.itemsChecked.append(planetId, true);
		}
		database.close();
	}

	public Object getItem(int position) {
		return this.planetMap.get(position);
	};
	
	public View getView(final int pos, View row, ViewGroup parent) {
		if( row == null ) {
			row = (View) LayoutInflater.from(this.context).inflate(R.layout.destinations_listview_item, 
					parent, false);
		}

		Map<String, Object> planet = this.planetMap.get(pos);
		String planetName = (String) planet.get("planetName");
		int resourceId = ((Integer) planet.get("planetImage")).intValue();
		final int planetId = ((Integer) planet.get("planetId")).intValue();
		
		TextView tv = (TextView) row.findViewById(R.id.destinations_listview_item_planet_name);
		tv.setText(planetName);

		ImageView iv = (ImageView) row.findViewById(R.id.destinations_listview_item_planet_image);
		iv.setImageResource(resourceId);

		CheckBox cn = (CheckBox) row.findViewById(R.id.destinations_listview_item_planet_check);
		cn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				boolean isChecked = cb.isChecked();
				if( isChecked ) {
					itemsChecked.append(planetId, true);
				} else {
					itemsChecked.delete(planetId);
				}
				listener.onCheckBoxStateChanged(isChecked, planetId);
			}
		});
		cn.setChecked(this.itemsChecked.get(planetId, false));

		return row;
	}
	
	public int getAmountOfCheckboxesSelected() {
		return this.itemsChecked.size();
	}
}
