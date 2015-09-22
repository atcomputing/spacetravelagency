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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Preferences {
	public static final String KEY_PREF_ENABLE_SOUNDS = "pref_enableSounds";
	public static final String KEY_PREF_ANIMATE_BACKGROUND = "pref_animateBackground";
	public static final String KEY_PREF_STARTUP_ANIMATION = "pref_showSplashscreenAnimation";
	public static final String KEY_PREF_FIRST_TIME_RUN = "firstTimeRun";
	private static boolean enableSounds = false;
	private static boolean enableBackgroundAnimation = false;
	private static boolean enableSplashscreen = false;
	private static boolean firstTimeRun = true;
	
	public static void load(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		enableBackgroundAnimation = sp.getBoolean(KEY_PREF_ANIMATE_BACKGROUND, false);
		enableSounds = sp.getBoolean(KEY_PREF_ENABLE_SOUNDS, false);
		enableSplashscreen = sp.getBoolean(KEY_PREF_STARTUP_ANIMATION, false);
		firstTimeRun = sp.getBoolean(KEY_PREF_FIRST_TIME_RUN, true);
	}
	
	public static void set(SharedPreferences sharedPreferences, String key) {
		if (key.equals(KEY_PREF_ENABLE_SOUNDS)) {
            enableSounds = sharedPreferences.getBoolean(key, true);
        } else if (key.equals(KEY_PREF_ANIMATE_BACKGROUND)) {
        	enableBackgroundAnimation = sharedPreferences.getBoolean(key, false);
        } else if (key.equals(KEY_PREF_STARTUP_ANIMATION)) {
        	enableSplashscreen = sharedPreferences.getBoolean(key, false);
        }
	}
	
	public static void savePreference(Context context, String key, boolean value) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = sp.edit().putBoolean(key, value);
		edit.apply();
	}
	
	public static boolean soundEnabled() {
		return enableSounds;
	}
	
	public static boolean animateBackground() {
		return enableBackgroundAnimation;
	}
	
	public static boolean showSplashScreen() {
		return enableSplashscreen;
	}
	
	 public static boolean firstTimeRun() {
		 return firstTimeRun;
	 }
}
