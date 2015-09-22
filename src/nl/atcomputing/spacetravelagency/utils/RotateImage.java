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

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class RotateImage  {
	
	private static double radToDegreesFactor = 180.0/Math.PI;
	
	public static Bitmap rotate(Bitmap bm, float x, float y) {
//		Matrix mat = new Matrix();
		float degrees = (float) (Math.atan2(y, x) * radToDegreesFactor);
//		mat.postRotate(degrees);
//		Bitmap bmRotate = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), mat, true);
		
		int width = bm.getWidth();
		int height = bm.getHeight();
		
		Bitmap bmRotate = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmRotate);
		canvas.rotate(degrees, width / 2, width / 2); 
		canvas.drawBitmap(bm, 0, 0, null); 
		
		return bmRotate;
	}
}
