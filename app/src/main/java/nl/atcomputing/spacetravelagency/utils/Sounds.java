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

import java.util.HashMap;

import nl.atcomputing.spacetravelagency.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class Sounds {
	public enum Type {
		DESELECTITEM, DOCK, SELECTITEM, SPOKENWORD_AGENCY, SPOKENWORD_TRAVEL, SPOKENWORD_SPACE,
		SPOKENWORD_WORKING, ITEMPRESS, FAIL, PROCESSING, COMPLETE, STARTACTIVITY, BACK
	}

	private static Sounds instance;
	private SoundPool soundPool;
	private int[] soundPoolArray;
	private HashMap<Type, Integer> rawSounds;
	
	private float volume;
	private Context context;

	private PlayLoopSound playLoopSound;
	
	public static Sounds getInstance(Context context) {
		if( instance == null ) {
			instance = new Sounds(context);
		}
		return instance;
	}

	protected Sounds(Context context) {
		this.context = context;
		int soundsAmount = Type.values().length;

		this.rawSounds = new HashMap<Sounds.Type, Integer>();
		this.soundPoolArray = new int[soundsAmount];
		this.soundPool = new SoundPool(soundsAmount, AudioManager.STREAM_MUSIC, 10);
		
		setupSound(Type.SPOKENWORD_SPACE,R.raw.spoken_space);
		setupSound(Type.SPOKENWORD_TRAVEL,R.raw.spoken_travel);
		setupSound(Type.SPOKENWORD_AGENCY,R.raw.spoken_agency);
		setupSound(Type.DESELECTITEM,R.raw.deselectitem);
		setupSound(Type.DOCK,R.raw.dock);
		setupSound(Type.SELECTITEM,R.raw.selectitem);
		setupSound(Type.SPOKENWORD_WORKING,R.raw.working);
		setupSound(Type.ITEMPRESS,R.raw.itempress);
		setupSound(Type.FAIL,R.raw.fail);
		setupSound(Type.PROCESSING,R.raw.processing);
		setupSound(Type.COMPLETE,R.raw.complete);
		setupSound(Type.STARTACTIVITY,R.raw.startactivity);
		setupSound(Type.BACK,R.raw.back);

		AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 4f;
		volume = streamVolumeCurrent / streamVolumeMax;
	}

	private void setupSound(Type sound, int resid) {
		this.rawSounds.put(sound, resid);
		this.soundPoolArray[sound.ordinal()] = soundPool.load(this.context, resid, 1);
	}
	
	/**
	 * Play sound at default volume
	 * @param sound 
	 * @return streamId of sound in SoundPool or -1 if sounds are disabled
	 */
	public int play(Type sound) {
		if( Preferences.soundEnabled() ) {
			return soundPool.play(this.soundPoolArray[sound.ordinal()], this.volume, this.volume, 1, 0, 1f);
		} else {
			return -1;
		}
	}

	/**
	 * Play sound at specified volume
	 * @param sound
	 * @param volumePercentage from 0.0 until 1.0
	 * @return streamId of sound in SoundPool or -1 if sounds are disabled
	 */
	public int play(Type sound, float volumePercentage) {
		if( Preferences.soundEnabled() ) {
			float vol = this.volume * volumePercentage;
			return soundPool.play(this.soundPoolArray[sound.ordinal()], vol, vol, 1, 0, 1f);
		} else {
			return -1;
		}
	}

	/**
	 * Play sound at default volume
	 * @param sound
	 * @param loop loop mode (0 = no loop, -1 = loop forever)
	 * @return streamId of sound in SoundPool or -1 if sounds are disabled
	 */
	public int play(Type sound, int loop) {
		if( Preferences.soundEnabled() ) {
			return soundPool.play(this.soundPoolArray[sound.ordinal()], this.volume, this.volume, 1, loop, 1f);
		} else {
			return -1;
		}
	}

	public void release() {
		soundPool.release();
	}

	/**
	 * Stop the stream specified by the streamID.
	 * @param streamID
	 */
	public void stop(int streamID) {
		soundPool.stop(streamID);
	}

	public void playLoop(Type sound) {
		this.playLoopSound = new PlayLoopSound(sound);
		this.playLoopSound.start();
	}

	public void stopLoop() {
		if( this.playLoopSound != null ) {
			this.playLoopSound.stopPlaying();
		}
	}
	
	private class PlayLoopSound extends Thread {
		private boolean keepPlaying = true;
		private Type sound;
		private int streamID;
		private int duration;
		
		public PlayLoopSound(Type sound) {
			super();
			this.sound = sound;
			MediaPlayer mp = MediaPlayer.create(context, rawSounds.get(sound));
			this.duration = mp.getDuration();
			mp.release();
		}

		@Override
		public void run() {
			while(keepPlaying) {
				this.streamID = soundPool.play(soundPoolArray[this.sound.ordinal()], volume, volume, 1, 0, 1f);
				try {
					Thread.sleep((long) (this.duration - 100L)); // -100 ms to reduce the gaps
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void stopPlaying() {
			this.keepPlaying = false;
			soundPool.stop(streamID);
		}
	}
}
