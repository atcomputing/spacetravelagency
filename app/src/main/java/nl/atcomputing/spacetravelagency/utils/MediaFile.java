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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class MediaFile {

	public static String STORAGE_DIR = "SpaceTravelAgency";
	
	public static enum Type {
		IMAGE, VIDEO
	}

	/**
	 * Create a Uri for saving an image or video
	 * @param type The type of file you want to save. This can be Type.IMAGE or Type.VIDEO.
	 * @return Uri holding the location of the media on external storage or null if the location could not be created
	 */
	public static Uri getOutputMediaFileUri(MediaFile.Type type){
		File file = getOutputMediaFile(type);
		if( file != null ) {
	      return Uri.fromFile(file);
		} 
		return null;
	}

	/**
	 * Create a File for saving an image or video
	 * @param type The type of file you want to save. This can be Type.IMAGE or Type.VIDEO.
	 * @return File holding the location of the media on external storage or null if the location could not be created
	 */
	public static File getOutputMediaFile(MediaFile.Type type){
		
	    //Check that external storage is available
		if ( ! Environment.getExternalStorageState().contentEquals(Environment.MEDIA_MOUNTED) ) {
			return null;
		}

	    File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), STORAGE_DIR);
	    
	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.e("SpaceTravelAgency", "failed to create directory " + mediaStorageDir.getAbsolutePath());
	            return null;
	        }
	    }

	    // Create a media file name using the current date and time
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
	    File mediaFile;
	    if (type == Type.IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == Type.VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
}
