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
