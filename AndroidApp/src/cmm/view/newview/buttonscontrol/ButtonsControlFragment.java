package cmm.view.newview.buttonscontrol;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import cmm.view.R;
import cmm.view.newview.CmmActivity;

public class ButtonsControlFragment extends Fragment {
	private static final String TAG = "ButtonsControlFragment";

	/* Rate button ratio */
	private static final double RATE_BUTTON_CONSTANT = 0.20;

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
		View view = inflater.inflate(R.layout.buttonscontrol_fragment,
				container, false);

		this.ui_view = view;

		setupComponents();
		handleEvents();
		doLayout();

		return view;
	}

	public void EnableButton() {
		this.ui_up.setEnabled(true);
		this.ui_down.setEnabled(true);

		this.ui_up.setBackgroundResource(R.drawable.thumbbutton_up);

		this.ui_down.setBackgroundResource(R.drawable.thumbbutton_down);
	}

	public void DisableButton(boolean isThumbsUp) {
		this.ui_up.setEnabled(false);
		this.ui_down.setEnabled(false);

		if (isThumbsUp) {

			this.ui_up.setBackgroundResource(R.drawable.thumbbutton_up_pressed);
			this.ui_down
					.setBackgroundResource(R.drawable.thumbbutton_down_disabled);
		} else {
			this.ui_up
					.setBackgroundResource(R.drawable.thumbbutton_up_disabled);
			this.ui_down
					.setBackgroundResource(R.drawable.thumbbutton_down_pressed);
		}
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
		Log.d(TAG, "up width = " + ui_up.getWidth());
		this.ui_up.setHeight((int) (RATE_BUTTON_CONSTANT * activity
				.getDimension().x));
		this.ui_down.setHeight((int) (RATE_BUTTON_CONSTANT * activity
				.getDimension().x));
	}
}
