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