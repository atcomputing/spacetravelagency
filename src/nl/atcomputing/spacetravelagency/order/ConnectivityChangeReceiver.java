package nl.atcomputing.spacetravelagency.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		DepartureInfoService.cancelDepartureInfoServiceAlarm(context);
		DepartureInfoService.setupDepartureInfoServiceAlarm(context);
	}

}
