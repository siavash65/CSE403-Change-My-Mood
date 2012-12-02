package cmm.view.newview.buttonscontrol;

import android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ButtonsControlFragment extends Fragment {
	private static final String TAG = "ButtonsControlFragment";
	
	private static ButtonsControlFragment instance;
	
	public static ButtonsControlFragment getInstance() {
		if (instance == null) {
			instance = new ButtonsControlFragment();
		}
		return instance;
	}
	
	private ButtonsControlFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//View view = inflater.inflate(R.layout.buttonscontrol_fragment, container, false);
		
		return null;
	}
}
