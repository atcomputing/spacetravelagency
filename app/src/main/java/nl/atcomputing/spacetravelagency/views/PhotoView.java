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

package nl.atcomputing.spacetravelagency.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PhotoView extends ImageView {

	public PhotoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		String filename = (String) getTag();

		if( filename != null ) {
			Bitmap bm = loadBitmap(filename, getWidth(), getHeight());

			setImageBitmap(bm);
		}
	}

	private Bitmap loadBitmap(String filename, int maxWidth, int maxHeight) {

		//Get height and width of image
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);
		final int height = options.outHeight;
		final int width = options.outWidth;

		//Calculate sampling size to match image size with view size
		//to prevent out of memory errors
		options.inSampleSize = 1;
		if (width > maxWidth || height > maxHeight) {
			if (width > height) {
				options.inSampleSize = Math.round((float)height / (float)maxHeight);
			} else {
				options.inSampleSize = Math.round((float)width / (float)maxWidth);
			}
		}

		options.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(filename, options);

		return bm;
	}

}
