package cmm.view.newview.buttonscontrol;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import cmm.view.R;
import cmm.view.newview.CmmActivity;

/**
 * This class is the class that manages the thumbsup/down
 * buttons
 * @author hunlan
 */
public class ButtonsControlFragment extends Fragment {
	private static final String TAG = "ButtonsControlFragment";

	/* Rate button ratio (width/height) */
	private static final double RATE_BUTTON_CONSTANT = 0.20;

	/* Singleton instance */
	private static ButtonsControlFragment instance;

	/* UI components */
	private View ui_view;
	private Button ui_up;
	private Button ui_down;

	private CmmActivity activity;

	/**
	 * Singleton constructor, pls pass us CmmActivity so we could update it
	 * @param activity
	 * @return Instance of ButtonControlFragment
	 */
	public static ButtonsControlFragment getInstance(CmmActivity activity) {
		if (instance == null) {
			instance = new ButtonsControlFragment(activity);
		}
		return instance;
	}

	// part of singletion pattern
	private ButtonsControlFragment(CmmActivity activity) {
		super();
		this.activity = activity;
	}

	/**
	 * When fragment starts up
	 */
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

	/**
	 * Enable the Thumbs button
	 */
	public void EnableButton() {
		if (ui_up.getVisibility() == View.INVISIBLE) {
			ui_up.setVisibility(View.VISIBLE);
		}
		if (ui_down.getVisibility() == View.INVISIBLE) {
			ui_down.setVisibility(View.VISIBLE);
		}
		this.ui_up.setEnabled(true);
		this.ui_down.setEnabled(true);

		this.ui_up.setBackgroundResource(R.drawable.thumbbutton_up);

		this.ui_down.setBackgroundResource(R.drawable.thumbbutton_down);
	}

	/**
	 * Disable the thumbs button and depending on isThumbsUp, show 
	 * that the button is pressed
	 * @param isThumbsUp
	 */
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

	/**
	 * Display a toast response to user
	 * @param isSuccess
	 */
	public void displayResponse(boolean isSuccess) {
		activity.displayRateResponse(isSuccess);
	}

	/**
	 * Setup components, assigning ui components to actual view objects in layout
	 */
	private void setupComponents() {
		this.ui_up = (Button) ui_view.findViewById(R.id.thumbsup_button);
		this.ui_down = (Button) ui_view.findViewById(R.id.thumbsdown_button);
	}

	// nothing here
	private void handleEvents() {

	}

	/**
	 * Organize layout - Resize the thumbing buttons
	 */
	private void doLayout() {
		Log.d(TAG, "up width = " + ui_up.getWidth());
		this.ui_up.setHeight((int) (RATE_BUTTON_CONSTANT * activity
				.getDimension().x));
		this.ui_down.setHeight((int) (RATE_BUTTON_CONSTANT * activity
				.getDimension().x));
		ui_up.setVisibility(View.INVISIBLE);
		ui_down.setVisibility(View.INVISIBLE);
	}
}
