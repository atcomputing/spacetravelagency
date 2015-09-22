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

package nl.atcomputing.spacetravelagency.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;


public class Database  {
	private static final int DATABASE_VERSION = 3;
	private DatabaseHelper dbHelper;
	private Context context;
	private String starSystem;
	private final String TAG = "Database";
	private SQLiteDatabase db;

	public Database(Context context, String starSystem) {
		this.context = context;
		this.starSystem = starSystem;
	}

	public Database open() {
		dbHelper = new DatabaseHelper(context, DatabaseHelper.DATABASE_NAME, null, DATABASE_VERSION);
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException e) {
			Log.e(TAG, "Could not get writable database " + DatabaseHelper.DATABASE_NAME);
			throw e;
		}
		return this;
	}

	public void upgrade() {
		dbHelper.onUpgrade(db, 1, DATABASE_VERSION);
	}

	public void close() {
		dbHelper.close();
	}

	public long addDestination(int planetId, String planetName, int price, int distance) {
		long rowId = getRowId(planetId);
		if( rowId > -1 ) { // item already in database
			return rowId;
		}
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.Destinations.STAR_SYSTEM, this.starSystem);
		values.put(DatabaseHelper.Destinations.DESTINATION, planetName);
		values.put(DatabaseHelper.Destinations.DESTINATION_ID, planetId);
		values.put(DatabaseHelper.Destinations.PRICE, price);
		values.put(DatabaseHelper.Destinations.DISTANCE, distance);
		return db.insert(DatabaseHelper.Destinations.TABLE_NAME, null, values);
	}

	public int removeDestination(int planetId) {
		return db.delete(DatabaseHelper.Destinations.TABLE_NAME, 
				DatabaseHelper.Destinations.STAR_SYSTEM + "= ? AND " +
						DatabaseHelper.Destinations.DESTINATION_ID + "= ?",
						new String[] {this.starSystem, String.valueOf(planetId)});
	}

	public Cursor getDestinations() {
		Cursor mCursor = db.query(true, DatabaseHelper.Destinations.TABLE_NAME, 
				new String[] {
				DatabaseHelper.Destinations.STAR_SYSTEM,
				DatabaseHelper.Destinations.DESTINATION,
				DatabaseHelper.Destinations.DESTINATION_ID,
				DatabaseHelper.Destinations.PRICE,
				DatabaseHelper.Destinations.DISTANCE
		},
		DatabaseHelper.Destinations.STAR_SYSTEM + "= ?", 
		new String[] {this.starSystem}, null, null, null, null);
		return mCursor;
	}
	
	public Cursor getAllDestinations() {
		Cursor mCursor = db.query(true, DatabaseHelper.Destinations.TABLE_NAME, 
				new String[] {
				DatabaseHelper.Destinations.STAR_SYSTEM,
				DatabaseHelper.Destinations.DESTINATION,
				DatabaseHelper.Destinations.DESTINATION_ID,
				DatabaseHelper.Destinations.PRICE,
				DatabaseHelper.Destinations.DISTANCE
		},
		null, null, null, null, null, null);
		return mCursor;
	}
	
	public boolean isSelected(int planetId) {
		Cursor mCursor = db.query(true, DatabaseHelper.Destinations.TABLE_NAME, 
				new String[] {
				DatabaseHelper.Destinations.DESTINATION
		},
		DatabaseHelper.Destinations.STAR_SYSTEM + "= ? AND " +
				DatabaseHelper.Destinations.DESTINATION_ID + "= ?", 
				new String[] { this.starSystem, String.valueOf(planetId) }, 
				null, null, null, null);
		return mCursor.getCount() > 0;
	}

	private long getRowId(int planetId) {
		Cursor mCursor = db.query(true, DatabaseHelper.Destinations.TABLE_NAME, 
				new String[] {
				DatabaseHelper.Destinations._ID
		},
		DatabaseHelper.Destinations.STAR_SYSTEM + "= ? AND " +
				DatabaseHelper.Destinations.DESTINATION_ID + "= ?", 
				new String[] { this.starSystem, String.valueOf(planetId) }, 
				null, null, null, null);
		if( mCursor.moveToFirst() ) {
			return mCursor.getLong(mCursor.getColumnIndex(DatabaseHelper.Destinations._ID));
		} else {
			return -1;
		}
	}

	public long addOrder(String orderId, long departureTime,
			String departureLocation) {
		if( getOrder(orderId).getCount() > 0 ) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.DepartureInfo.ORDER_ID, orderId);
		values.put(DatabaseHelper.DepartureInfo.DEPARTURE_TIME, departureTime);
		values.put(DatabaseHelper.DepartureInfo.DEPARTURE_LOCATION,
				departureLocation);
		return db.insert(DatabaseHelper.DepartureInfo.TABLE_NAME, null, values);
	}

	public long deleteOrder(String orderId) {
		return db.delete(DatabaseHelper.DepartureInfo.TABLE_NAME,
				DatabaseHelper.DepartureInfo.ORDER_ID + "= ? ",
				new String[] { orderId });
	}

	public Cursor getOrder(String orderId) {
		Cursor mCursor = db.query(true, DatabaseHelper.DepartureInfo.TABLE_NAME,
				new String[] {
				DatabaseHelper.DepartureInfo.DEPARTURE_LOCATION,
				DatabaseHelper.DepartureInfo.DEPARTURE_TIME
		},
		DatabaseHelper.DepartureInfo.ORDER_ID + "= ? ",
		new String[] { orderId },
		null, null, null, null);
		return mCursor;
	}

	public long updateOrder(String orderId, long departureTime,
			String departureLocation) {
		if( getOrder(orderId).getCount() == 0 ) {
			return addOrder(orderId, departureTime, departureLocation);
		} else {
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.DepartureInfo.ORDER_ID, orderId);
			values.put(DatabaseHelper.DepartureInfo.DEPARTURE_TIME, departureTime);
			values.put(DatabaseHelper.DepartureInfo.DEPARTURE_LOCATION,
					departureLocation);
			return db.update(DatabaseHelper.DepartureInfo.TABLE_NAME, values,
					DatabaseHelper.DepartureInfo.ORDER_ID + "= ? ",
					new String[] { orderId });
		}
	}

	public boolean orderChanged(String orderId, long departureTime,
			String departureLocation) {
		if( departureLocation == null ) {
			return false;
		}

		Cursor mCursor = getOrder(orderId);
		if( mCursor.moveToFirst() ) {
			int index =
					mCursor.getColumnIndex(DatabaseHelper.DepartureInfo.DEPARTURE_LOCATION);
			String location = mCursor.getString(index);
			index =
					mCursor.getColumnIndex(DatabaseHelper.DepartureInfo.DEPARTURE_TIME);
			long time = Long.parseLong(mCursor.getString(index));

			if( location == null ) {
				return true;
			}
			
			if( departureLocation.contentEquals(location) &&
					(time == departureTime) ) {
				return false;
			} else {
				return true;
			}
		}
		// No order available in database. So cannot check if anything changed really.
		return false;
	}

}