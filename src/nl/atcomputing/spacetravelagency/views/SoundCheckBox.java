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
