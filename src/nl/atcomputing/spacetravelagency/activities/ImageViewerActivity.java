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

package nl.atcomputing.spacetravelagency.activities;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.app.SoundActivity;
import nl.atcomputing.spacetravelagency.views.PhotoView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class ImageViewerActivity extends SoundActivity  {
	public static String BUNDLE_KEY_FILEPATH = "bundleFilePath";
	
	private PhotoView iv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Remove title bar (action bar)
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.imagevieweractivity);

		String filename = getIntent().getStringExtra(BUNDLE_KEY_FILEPATH);
		if( filename != null ) {
			this.iv = (PhotoView) findViewById(R.id.imageview);		
			this.iv.setTag(filename);
		}
	}
}
