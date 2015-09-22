package nl.atcomputing.spacetravelagency.views;

import nl.atcomputing.spacetravelagency.utils.Sounds;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class SoundCheckBox extends CheckBox {
	
	private Sounds sounds;
	
	public SoundCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.sounds = Sounds.getInstance(context);
	}

	@Override
	public boolean performClick() {
		if( isChecked() ) {
			this.sounds.play(Sounds.Type.DESELECTITEM);
		} else {
			this.sounds.play(Sounds.Type.SELECTITEM);
		}
		return super.performClick();
	}
}
