package nl.atcomputing.spacetravelagency.activities;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.utils.Preferences;
import nl.atcomputing.spacetravelagency.utils.Sounds;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends Activity implements AnimationListener {
	private ImageView imageView;

	private int[] images = {
			R.drawable.space,
			R.drawable.travel,
			R.drawable.agency
	};

	private Sounds.Type[] animSounds = {
			Sounds.Type.SPOKENWORD_SPACE,
			Sounds.Type.SPOKENWORD_TRAVEL,
			Sounds.Type.SPOKENWORD_AGENCY
	};
	
	private int index;

	private Sounds sounds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Preferences.load(this);
		
		if (Preferences.showSplashScreen() || Preferences.firstTimeRun() ) {
			Preferences.savePreference(this, Preferences.KEY_PREF_FIRST_TIME_RUN, false);
		} else {
			startNextActivity();
		}

		setContentView(R.layout.splashscreenactivity);

		this.sounds = Sounds.getInstance(this);
		
		this.imageView = (ImageView) findViewById(R.id.imageview);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.slideinout);
		anim.setAnimationListener(this);
		this.imageView.startAnimation(anim);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		this.index++;
		if( this.index < (this.images.length - 1)) {
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.slideinout);
			this.imageView.startAnimation(anim);
			anim.setAnimationListener(this);
		} else if( this.index < this.images.length ) {
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.slidein);
			anim.setFillAfter(true);
			this.imageView.startAnimation(anim);
			anim.setAnimationListener(this);
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slidein, R.anim.slideout);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {
		this.imageView.setImageResource(this.images[index]);
		this.sounds.play(animSounds[index]);
	}

	public void startNextActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
