package nl.atcomputing.spacetravelagency.adapters;

import java.io.File;
import java.io.FilenameFilter;

import nl.atcomputing.spacetravelagency.utils.MediaFile;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PictureAdapter extends BaseAdapter {
	private Context context;
	private File[] imageFiles;
	
    public PictureAdapter(Context c) {
        this.context = c;
        
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), MediaFile.STORAGE_DIR);
	    FilenameFilter filter = new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith("-thumb.jpg");
			}
		};
		
		this.imageFiles = mediaStorageDir.listFiles(filter);
    }

    public int getCount() {
    	int count = 0;
    	if ( this.imageFiles != null ) {
    		count = this.imageFiles.length;
    	}
        return count;
    }

    /**
     * Returns the File object of the image at the specified position
     * @return File object
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return this.imageFiles[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
          		
        Bitmap bm = BitmapFactory.decodeFile(this.imageFiles[position].getAbsolutePath());
        
        if (convertView == null) {
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(bm.getWidth(), bm.getHeight()));
            imageView.setAdjustViewBounds(false);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(bm);
        
        return imageView;
    }
}

