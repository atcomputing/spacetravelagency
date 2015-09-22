package nl.atcomputing.spacetravelagency.activities;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.app.SoundActivity;
import nl.atcomputing.spacetravelagency.views.PhotoView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class ImageViewerActivity extends SoundActivity  {
	public static String BUNDLE_KEY_FILEPATH = "bundleFilePath";
	
	private PhotoView iv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Remove title bar (action bar)
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.imagevieweractivity);

		String filename = getIntent().getStringExtra(BUNDLE_KEY_FILEPATH);
		if( filename != null ) {
			this.iv = (PhotoView) findViewById(R.id.imageview);		
			this.iv.setTag(filename);
		}
	}
}
