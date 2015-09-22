package nl.atcomputing.spacetravelagency.activities;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.app.SoundSherlockFragmentActivity;
import nl.atcomputing.spacetravelagency.fragments.CheckoutFragment;
import nl.atcomputing.spacetravelagency.order.HttpCommunicator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CheckoutActivity extends SoundSherlockFragmentActivity implements OnClickListener {
	
	private BroadcastReceiver broadcastReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.checkoutactivity);

		CheckoutFragment.add(R.id.fragment_container, getSupportFragmentManager());
		
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(this);
		
//		StateSingleton.getInstance().setCheckingOut();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateButton();
		this.broadcastReceiver = new BroadcastReceiver() {
	        public void onReceive(Context context, Intent intent) {
	        	updateButton();
	        }
	    };
		registerReceiver(broadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(this.broadcastReceiver);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button:
			Intent intent = new Intent(this, PlaceOrderActivity.class);
			startActivity(intent);
		}
	}
	
	private void updateButton() {
		Button button = (Button) findViewById(R.id.button);
		if( ! HttpCommunicator.isOnline(getApplicationContext()) ) {
			button.setEnabled(false);
			button.setText(R.string.enable_internet_to_book_journey);
		} else {
			button.setEnabled(true);
			button.setText(R.string.book_journey);
		}
	}
}
