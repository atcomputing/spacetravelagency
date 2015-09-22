package nl.atcomputing.spacetravelagency.order;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nl.atcomputing.spacetravelagency.order.Order.OrderListener;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;

class CancelOrderAsyncTask extends AsyncTask<Order, Order, ArrayList<Order>> {

	private Context context;
	private OrderListener listener;
	
	public CancelOrderAsyncTask(Context context, OrderListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected ArrayList<Order> doInBackground(Order... params) {
		ArrayList<Order> orders = new ArrayList<Order>();

		for( Order order : params ) {
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost("http://www.atcomputing.nl/spacetravelagency/order.php");
			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			String android_id = Secure.getString(this.context.getContentResolver(),
					Secure.ANDROID_ID);
			postParameters.add(new BasicNameValuePair("customerid", android_id));

			postParameters.add(new BasicNameValuePair("destinations", ""));

			try {
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
				request.setEntity(formEntity);
				HttpResponse response = client.execute(request);
				parseResponse(response, order);
				publishProgress(order);
				orders.add(order);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return orders;
	}

	@Override
	protected void onProgressUpdate(Order... values) {
		this.listener.onOrderProcessed(values[0]);
	}
	
	@Override
	protected void onPostExecute(ArrayList<Order> result) {
	}
	
	private void parseResponse(HttpResponse response, Order order) {
		if( response == null ) {
			return;
		}

		try {
			InputStream is = response.getEntity().getContent();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader( isr );

			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();

			String[] tokens = sb.toString().split(":");

			// Check to prevent parsing misformed messages
			if( ( tokens.length > 1 ) && ( ( tokens.length % 2) == 0) ) {
				for( int i = 0; i < tokens.length; i = i + 2) {
					String keyword = tokens[i];
					String value = tokens[i+1];
					if( keyword.contentEquals("orderid") ) {
						order.setOrderId(value);
					} else if( keyword.contentEquals("orderconfirmed") &&
							value.contentEquals("true") ) {
						order.setConfirmed();
					} else if ( keyword.contentEquals("departuretime") ) {
						order.setDepartureTime(Long.parseLong(value));
					} else if ( keyword.contentEquals("location") ) {
						order.setDepartureLocation(value);
					} 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
