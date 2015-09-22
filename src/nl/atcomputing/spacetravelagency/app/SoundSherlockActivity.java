package nl.atcomputing.spacetravelagency.app;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.activities.PreferencesActivity;
import nl.atcomputing.spacetravelagency.dialogs.AboutDialog;
import nl.atcomputing.spacetravelagency.utils.Sounds;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SoundSherlockActivity extends SherlockActivity {

	protected Sounds sounds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.sounds = Sounds.getInstance(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
	
	@Override
	public void startActivity(Intent intent) {
		this.sounds.play(Sounds.Type.STARTACTIVITY);
		super.startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		this.sounds.play(Sounds.Type.BACK);
		super.onBackPressed();
	}
	
	public void playSoundItemClicked() {
		this.sounds.play(Sounds.Type.ITEMPRESS);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		this.sounds.play(Sounds.Type.STARTACTIVITY);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater m = new MenuInflater(this);
		m.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		playSoundItemClicked();
		Intent intent;
		switch(item.getItemId()) {
		case R.id.menu_about:
			AboutDialog dialog = AboutDialog.getInstance(this);
			dialog.show();
			return true;
		case R.id.menu_settings:
			intent = new Intent(this, PreferencesActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
