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

package nl.atcomputing.spacetravelagency.app;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.activities.PreferencesActivity;
import nl.atcomputing.spacetravelagency.dialogs.AboutDialog;
import nl.atcomputing.spacetravelagency.utils.Sounds;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

abstract public class SoundSherlockFragmentActivity extends SherlockFragmentActivity {

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
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		this.sounds.play(Sounds.Type.STARTACTIVITY);
		return super.onPrepareOptionsMenu(menu);
	}
	
	public void playSoundItemClicked() {
		this.sounds.play(Sounds.Type.ITEMPRESS);
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
	
	public Sounds getSounds() {
		return this.sounds;
	}
}
