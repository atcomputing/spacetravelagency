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

import java.util.Calendar;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.activities.PlaceOrderActivity;
import nl.atcomputing.spacetravelagency.database.Database;
import nl.atcomputing.spacetravelagency.order.Order.OrderListener;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;

public class DepartureInfoService extends Service implements OrderListener {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if( HttpCommunicator.isOnline(this) ) {
			Order order = new Order();
			String android_id = Secure.getString(getContentResolver(),
					Secure.ANDROID_ID);
			order.setOrderId(android_id);
			HttpCommunicator.retrieveDepartureInformation(this, this, order);
		}
		return START_NOT_STICKY;

	}

	private void sendNotification(long epochInMilliseconds) {

		/*
		 * Unclear what should replace it. Official docs do not mention FORMAT_24HOUR as deprecated
		 */
		@SuppressWarnings("deprecation") 
		CharSequence dateString = DateUtils.formatDateTime(this, 
				epochInMilliseconds, 
				DateUtils.FORMAT_24HOUR | 
				DateUtils.FORMAT_SHOW_DATE | 
				DateUtils.FORMAT_SHOW_TIME);

		Intent intent = new Intent(this, PlaceOrderActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		@SuppressWarnings("deprecation")
		Notification notification = builder.setContentIntent(pi)
		.setSmallIcon(R.drawable.ic_notification_icon)
		.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.spaceship_launcher))
		.setTicker(getString(R.string.departure_time_changed))
		.setWhen(System.currentTimeMillis())
		.setAutoCancel(true)
		.setContentTitle(getString(R.string.app_name))
		.setContentText(getString(R.string.departure_time_changed_to_) + dateString)
		.getNotification();

		nm.notify(1, notification);
	}

	static public void setupDepartureInfoServiceAlarm(Context context) {
		Intent intent = new Intent(context, DepartureInfoService.class);
		PendingIntent pi = PendingIntent.getService(context, 1, intent, 0);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		am.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(), 600000, pi);
	}

	static public void cancelDepartureInfoServiceAlarm(Context context) {
		Intent intent = new Intent(context, DepartureInfoService.class);
		PendingIntent pi = PendingIntent.getService(context, 1, intent, 0);
		AlarmManager am =
				(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}

	@Override
	public void onOrderProcessed(Order order) {
	}

	@Override
	public void onDepartureInformationReceived(Order order) {
		long epoch = order.getDepartureTime();
		if( epoch > -1 ) {
			String orderId = order.getOrderId();
			String location = order.getDepartureLocation();
			Log.d("Database", "onDepartureInformationReceived: order="+order);
			Database db = new Database(this, "");
			db.open();
			
			/**
			 * Check if order is already in database and if not
			 * add it. Otherwise check if order changed and update
			 * accordingly.
			 */
			Cursor cursor = db.getOrder(orderId);
			if( cursor.moveToFirst() ) {
				if( db.orderChanged(orderId, epoch, location)) {
					db.updateOrder(orderId, epoch, location);
					sendNotification(epoch * 1000);
				}
			} else {
				db.addOrder(orderId, epoch, location);
			}
			db.close();
		}
	}
}
