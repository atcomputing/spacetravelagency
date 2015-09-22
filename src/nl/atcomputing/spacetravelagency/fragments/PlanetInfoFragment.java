package nl.atcomputing.spacetravelagency.fragments;

import nl.atcomputing.spacetravelagency.R;
import nl.atcomputing.spacetravelagency.StateSingleton;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class PlanetInfoFragment extends SherlockFragment implements OnClickListener {
	public static String ARG_KEY_TEXT = "arg_key_text";
	private View view;
	private TextView textView;
	private OnScrollableTextViewFragmentListener listener;
	
	public interface OnScrollableTextViewFragmentListener {
		public void onTextViewClick();
	}
	
	public void show(String text) {
		if( this.textView != null ) {
			this.textView.setText(text);
		}
	}
	
	public void setListener(OnScrollableTextViewFragmentListener listener) {
		this.listener = listener;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.scrollable_textview_fragment, container, false);
		return this.view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		StateSingleton.getInstance().setShowingPlanetInfo();
		
		this.textView = (TextView) this.view.findViewById(R.id.textview);

		this.textView.setOnClickListener(this);
		
		Bundle bundle = getArguments();
		if( bundle != null ) {
			show(bundle.getString(ARG_KEY_TEXT));
		}
	}

	@Override
	public void onClick(View v) {
		if( this.listener != null ) {
			this.listener.onTextViewClick();
		}
	}
}