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

package nl.atcomputing.spacetravelagency.order;

import java.util.ArrayList;

import nl.atcomputing.spacetravelagency.database.Database;
import nl.atcomputing.spacetravelagency.database.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;


public class Order {
	public interface OrderListener {
		/**
		 * Called when order has been confirmed or not
		 * @param order
		 */
		public void onOrderProcessed(Order order);

		/**
		 * Called when departure information has been received
		 * @param order
		 */
		public void onDepartureInformationReceived(Order order);
	}

	private enum STATES {
		UNKOWN, CONFIRMING, NOT_CONFIRMED, CONFIRMED
	}
	private STATES state = STATES.UNKOWN;

	private ArrayList<Integer> destinations;
	private String orderId;
	private long departureTime;
	private String departureLocation;

	public ArrayList<Integer> getDestinations() {
		return destinations;
	}

	public void setDestinations(ArrayList<Integer> destinations) {
		this.destinations = destinations;
	}

	/**
	 * 
	 * @return Departure location or null if not available
	 */
	public String getDepartureLocation() {
		return departureLocation;
	}

	public void setDepartureLocation(String departureLocation) {
		this.departureLocation = departureLocation;
	}

	/**
	 * @return epoch value of departuretime or 0 if not available
	 */
	public long getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(long departureTime) {
		this.departureTime = departureTime;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void fillFromDatabase(Context context) {
		Database db = new Database(context, "");
		db.open();
		Cursor cursor = db.getDestinations();
		int indexDestination = cursor.getColumnIndex(DatabaseHelper.Destinations.DESTINATION);
		this.destinations = new ArrayList<Integer>();
		while (cursor.moveToNext()) {
			this.destinations.add(cursor.getInt(indexDestination));
		}

		cursor.close();
		db.close();
	}

	public void setConfirming() {
		this.state = STATES.CONFIRMING;
	}

	public boolean isConfirming() {
		return this.state == STATES.CONFIRMING;
	}

	public void setConfirmed() {
		this.state = STATES.CONFIRMED;
	}

	public boolean isConfirmed() {
		return this.state == STATES.CONFIRMED;
	}

	public void setNotConfirmed() {
		this.state = STATES.NOT_CONFIRMED;
	}

	public boolean isNotConfirmed() {
		return this.state == STATES.NOT_CONFIRMED;
	}

	/**
	 * Checks if all information is available in the order
	 * @return true if everything is available, false otherwise
	 */
	public boolean isComplete() {
		if( ( this.departureLocation == null )
				|| ( this.departureTime == 0 ) 
				|| ( this.orderId == null ) ) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String toString() {
		String string = "Order: id="+orderId+" "+
				", departureLocation="+departureLocation+", departureTime="+departureTime+
				", state="+this.state.name();
		return string;
	}
}
