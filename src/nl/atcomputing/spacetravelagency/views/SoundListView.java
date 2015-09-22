package nl.atcomputing.spacetravelagency.views;

import nl.atcomputing.spacetravelagency.utils.Sounds;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class SoundListView extends ListView {
	
	private Sounds sounds;
	
	public SoundListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.sounds = Sounds.getInstance(context);
	}

	@Override
	public boolean performItemClick(View view, int position, long id) {
		this.sounds.play(Sounds.Type.ITEMPRESS);
		return super.performItemClick(view, position, id);
	}
}
