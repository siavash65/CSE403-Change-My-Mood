package cmm.view.newview.navigation;

import java.util.Collection;
import java.util.HashSet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import cmm.view.R;

public class NavigationFragment extends Fragment {
	private Collection<Button> ui_moodButtons;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.navigation_fragment, container,
				false);

		setupComponents(view);
		handleEvents(view);
		doLayout(view);

		return view;
	}

	public void moodClick(View view) {
		// get an instance of FragmentTransaction from your Fragment
		FragmentTransaction fragmentTransaction = this.getFragmentManager()
				.beginTransaction();

		// add a fragment
		ContentSelectionFragment csFragment = new ContentSelectionFragment();
		fragmentTransaction.add(R.id.content_fragment, csFragment);
		fragmentTransaction.commit();
	}

	/*
	 * Setup the ui components
	 */
	private void setupComponents(View view) {
		// setup mood buttons
		ui_moodButtons = new HashSet<Button>();
		ui_moodButtons.add((Button) view.findViewById(R.id.funny_button));
		ui_moodButtons.add((Button) view.findViewById(R.id.romantic_button));
		ui_moodButtons.add((Button) view.findViewById(R.id.enervating_button));
		ui_moodButtons.add((Button) view.findViewById(R.id.inspiring_button));

	}

	/*
	 * Add listeners if necessary
	 */
	private void handleEvents(View view) {
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
	private void doLayout(View view) {
		
	}
}


















// end