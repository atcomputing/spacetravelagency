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
import java.io.File;
import java.io.FileOutputStream;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.adapters.PictureAdapter;
import nl.atcomputing.spacetravelagency.app.SoundSherlockActivity;
import nl.atcomputing.spacetravelagency.utils.MediaFile;
import nl.atcomputing.spacetravelagency.utils.Preferences;
import nl.atcomputing.spacetravelagency.utils.RotateImage;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


public class ImageGalleryActivity extends SoundSherlockActivity implements OnItemClickListener, OnClickListener, SensorEventListener {

	private final String BUNDLE_KEY_FILEURI = "fileUri";

	private Uri fileUri;

	private Bitmap backgroundBitmapImage;
	private ImageView backgroundImageView;

	private SensorManager sensorManager;
	private Sensor sensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.imagegalleryactivity);

		if( savedInstanceState != null ) {
			this.fileUri = (Uri) savedInstanceState.getParcelable(BUNDLE_KEY_FILEURI);
		}

		Button button = (Button) findViewById(R.id.imagegallery_button);
		button.setOnClickListener(this);

		this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		this.sensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

		this.backgroundImageView = (ImageView) findViewById(R.id.background_image);
		this.backgroundBitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.milky_way);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if( Preferences.animateBackground() ) {
			this.sensorManager.registerListener(this, this.sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}

		GridView gv = (GridView) findViewById(R.id.imagegallery_gridview);
		gv.setAdapter(new PictureAdapter(this));
		gv.setOnItemClickListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BUNDLE_KEY_FILEURI, this.fileUri);
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.imagegallery_button ) {
			this.fileUri = MediaFile.getOutputMediaFileUri(MediaFile.Type.IMAGE);
			if( this.fileUri != null ) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, this.fileUri);
				startActivityForResult(intent, 0);
			} else {
				Toast.makeText(this, "External storage not available.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Bitmap bmRotated = RotateImage.rotate(this.backgroundBitmapImage, 
				-event.values[0], event.values[1]);
		if( this.backgroundImageView != null ) {
			this.backgroundImageView.setImageBitmap(bmRotated);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		int width = 240;

		String path = this.fileUri.getPath();
		Bitmap bm = BitmapFactory.decodeFile(path);

		if( bm == null ) {
			return;
		}
		
		//Make sure we maintain aspect ratio
		int height = (int) (((double) bm.getHeight() / (double) bm.getWidth()) * (double) width);
		Bitmap sbm = Bitmap.createScaledBitmap(bm, width, height, false);

		String thumbnailFilename = path.replace(".jpg", "-thumb.jpg");
		try {
			FileOutputStream out = new FileOutputStream(thumbnailFilename);
			sbm.compress(Bitmap.CompressFormat.JPEG, 80, out);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View imageView,
			int pos, long id) {
		playSoundItemClicked();
		PictureAdapter adapter = (PictureAdapter) arg0.getAdapter();
		String filename =
				((File) adapter.getItem(pos)).getAbsolutePath().replace("-thumb.jpg",
						".jpg");
		Intent intent = new Intent(this, ImageViewerActivity.class);
		intent.putExtra(ImageViewerActivity.BUNDLE_KEY_FILEPATH, filename);
		startActivity(intent);
	}

}
