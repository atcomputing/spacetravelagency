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
