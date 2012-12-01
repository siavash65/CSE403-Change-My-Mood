package cmm.view.newview.navigation;

import java.util.Collection;
import java.util.HashSet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import cmm.model.Mood;
import cmm.view.R;
import cmm.view.newview.CmmActivity;

public class NavigationFragment extends Fragment {
	private CmmActivity activity;
	
	private View ui_view;
	private Collection<Button> ui_moodButtons;
	private Button ui_currentButton;
	private ContentSelectionFragment csf;

	public NavigationFragment(CmmActivity activity) {
		this.activity = activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.navigation_fragment, container,
				false);

		this.ui_view = view;
		
		setupComponents();
		handleEvents();
		doLayout();

		return view;
	}

	public void moodClick(View view) {
		Button clickedButton = (Button) view;

		// Determine who clicked
		int button_id = clickedButton.getId();
		Mood mood = Mood.HAPPY;
		switch (button_id) {
		case R.id.romantic_button:
			mood = Mood.ROMANTIC;
			break;
		case R.id.inspiring_button:
			mood = Mood.INSPIRED;
			break;
		case R.id.enervating_button:
			mood = Mood.EXCITED;
			break;
		}

		
		boolean differentButton = this.ui_currentButton != clickedButton;
		if (differentButton) {
			// Disable Button
			if (ui_currentButton != null) {
				ui_currentButton.setBackgroundResource(R.drawable.mood_button);
				ui_currentButton.setTextColor(Color.DKGRAY);
			}
			
			// Enable Button
			this.ui_currentButton = clickedButton;
			ui_currentButton.setBackgroundResource(R.drawable.tab_button_select);
			ui_currentButton.setTextColor(Color.WHITE);
		}

		if (this.csf == null) {
			// get an instance of FragmentTransaction from your Fragment
			FragmentTransaction fragmentTransaction = this.getFragmentManager()
					.beginTransaction();

			// add a fragment
			ContentSelectionFragment csFragment = ContentSelectionFragment
					.getInstance(activity, mood);
			fragmentTransaction.add(R.id.content_select_fragment, csFragment);
			fragmentTransaction.commit();
			this.csf = csFragment;
		} else if (differentButton) {
			// get an instance of FragmentTransaction from your Fragment
			FragmentTransaction fragmentTransaction = this.getFragmentManager()
					.beginTransaction();

			// remove previous
			fragmentTransaction.remove(this.csf);

			// add a fragment
			ContentSelectionFragment csFragment = ContentSelectionFragment
					.getInstance(activity, mood);
			fragmentTransaction.add(R.id.content_select_fragment, csFragment);
			fragmentTransaction.commit();
			this.csf = csFragment;

		} else {
			this.csf.reselect();
		}

	}

	/*
	 * Setup the ui components
	 */
	private void setupComponents() {
		// setup mood buttons
		ui_moodButtons = new HashSet<Button>();
		ui_moodButtons.add((Button) ui_view.findViewById(R.id.funny_button));
		ui_moodButtons.add((Button) ui_view.findViewById(R.id.romantic_button));
		ui_moodButtons.add((Button) ui_view.findViewById(R.id.enervating_button));
		ui_moodButtons.add((Button) ui_view.findViewById(R.id.inspiring_button));

	}

	/*
	 * Add listeners if necessary
	 */
	private void handleEvents() {
		// Add listener to mood buttons
		for (Button b : ui_moodButtons) {
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					moodClick(v);
				}
			});
		}
	}

	/*
	 * Resize layout if necessary
	 */
	private void doLayout() {
		for (Button b : ui_moodButtons) {
			b.setTextColor(Color.DKGRAY);
		}
	}
}

// end