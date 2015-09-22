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

package nl.atcomputing.spacetravelagency.utils;

import java.util.ArrayList;

import nl.atcomputing.spacetravelagency.Planet;
import android.content.Context;
import android.os.AsyncTask;

public class CalculateTravellingOrder extends AsyncTask<Planet, Integer, Planet[]> {

	private CalculateTravellingOrderListener listener;
	private Sounds sounds;
	private boolean soundEnabled = false;

	public interface CalculateTravellingOrderListener {
		public void handleTravellingOrder(Planet[] result);
	}

	public CalculateTravellingOrder(Context context, CalculateTravellingOrderListener listener) {
		this.listener = listener;
		this.sounds = Sounds.getInstance(context);
	}

	public void setListener(CalculateTravellingOrderListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		playSound();
	}

	protected Planet[] doInBackground(Planet... params) {
		Booking booking = new Booking();
		ArrayList<Planet> planets = new ArrayList<Planet>();
		for( Planet p : params ) {
			planets.add(p);
		}
		ArrayList<Planet> planetNames = booking.getTravellingOrder(planets);
		return planetNames.toArray(new Planet[0]);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

	}

	protected void onPostExecute(Planet[] result) {
		if( this.soundEnabled ) {
			this.sounds.stopLoop();
			this.sounds.play(Sounds.Type.COMPLETE);
		}
		listener.handleTravellingOrder(result);
	}

	public void playSound() {
		if( Preferences.soundEnabled() ) {
			this.soundEnabled = true;
			this.sounds.playLoop(Sounds.Type.PROCESSING);
		}
	}

	public void stopSound() {
		this.soundEnabled = false;
		this.sounds.stopLoop();
	}
}