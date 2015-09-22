package nl.atcomputing.spacetravelagency.activities;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.app.SoundSherlockFragmentActivity;
import nl.atcomputing.spacetravelagency.fragments.PlanetInfoFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PlanetInfoActivity extends SoundSherlockFragmentActivity {
	public static String BUNDLE_KEY_PLANETINFO = "planetInfo";
	
	private String planetInfo;
	
	private PlanetInfoFragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.planetinfoactivity);
		
		Intent intent = this.getIntent();
		this.planetInfo = intent.getStringExtra(BUNDLE_KEY_PLANETINFO);
		if( this.planetInfo == null ) {
			Log.e("PlanetInfoActivity", "Please use intent.putExtra(PlanetInfoActivity.BUNDLE_KEY_PLANETINFO, String) to provide the text to show");
			this.planetInfo = getResources().getString(R.string.no_info_available);
		}

		this.fragment = (PlanetInfoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
		this.fragment.setRetainInstance(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		this.fragment.show(this.planetInfo);
	}
}
