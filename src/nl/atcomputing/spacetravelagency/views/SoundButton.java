package nl.atcomputing.spacetravelagency.views;

import nl.atcomputing.spacetravelagency.utils.Sounds;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class SoundButton extends Button {
	
	private Sounds sounds;
	
	public SoundButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.sounds = Sounds.getInstance(context);
	}

	@Override
	public boolean performClick() {
		this.sounds.play(Sounds.Type.ITEMPRESS);
		return super.performClick();
	}
}
