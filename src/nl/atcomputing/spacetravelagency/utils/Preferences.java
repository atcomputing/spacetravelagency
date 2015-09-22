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
