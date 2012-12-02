package cmm.view.newview.buttonscontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import cmm.view.R;
import cmm.view.newview.CmmActivity;

public class ButtonsControlFragment extends Fragment {
	private static final String TAG = "ButtonsControlFragment";
	
	private static ButtonsControlFragment instance;
	
	private View ui_view;
	private Button ui_up;
	private Button ui_down;
	
	private CmmActivity activity;
	
	public static ButtonsControlFragment getInstance(CmmActivity activity) {
		if (instance == null) {
			instance = new ButtonsControlFragment(activity);
		}
		return instance;
	}
	
	private ButtonsControlFragment(CmmActivity activity) {
		super();
		this.activity = activity;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.buttonscontrol_fragment, container,
				false);
		
		this.ui_view = view;
		
		setupComponents();
		handleEvents();
		doLayout();
		
		return view;
	}
	
	public void EnableButton() {
		this.ui_up.setEnabled(true);
		this.ui_down.setEnabled(true);
	}

	public void DisableButton() {
		this.ui_up.setEnabled(false);
		this.ui_down.setEnabled(false);
	}
	
	public void displayResponse(boolean isSuccess) {
		activity.displayRateResponse(isSuccess);
	}
	
	private void setupComponents() {
		this.ui_up = (Button) ui_view.findViewById(R.id.thumbsup_button);
		this.ui_down = (Button) ui_view.findViewById(R.id.thumbsdown_button);
	}
	
	private void handleEvents() {
		
	}
	private void doLayout() {
		
	}
}
