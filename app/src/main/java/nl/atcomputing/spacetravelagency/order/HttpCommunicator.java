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

import nl.atcomputing.spacetravelagency.order.Order.OrderListener;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpCommunicator {
	
	static public void retrieveDepartureInformation(Context context, OrderListener listener, Order order) {
		RetrieveOrderInformationAsyncTask task = new RetrieveOrderInformationAsyncTask(context, listener);
		task.execute(order);
	}

	static public void placeOrder(Context context, OrderListener listener, Order order) {
		PlaceOrderAsyncTask task = new PlaceOrderAsyncTask(listener);
		task.execute(order);
	}
	
	static public void cancelOrder(Context context, OrderListener listener, Order order) {
		CancelOrderAsyncTask task = new CancelOrderAsyncTask(context, listener);
		task.execute(order);
	}
	
	static public boolean isOnline(Context context) {
		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true; //We are connected
		}
		return false;
	}
}
