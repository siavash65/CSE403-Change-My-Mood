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
import cmm.view.R;

public class ContentSelectionFragment extends Fragment {
	private Collection<Button> ui_contentButtons;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.content_fragment, container,
				false);

		setupComponents(view);
		handleEvents(view);
		doLayout(view);

		return view;
	}

	public void contentClick(View view) {
		// get an instance of FragmentTransaction from your Fragment
		FragmentTransaction fragmentTransaction = this.getFragmentManager()
				.beginTransaction();

		// remove this fragment
		fragmentTransaction.remove(this);
		fragmentTransaction.commit();
	}

	/*
	 * Setup the ui components
	 */
	private void setupComponents(View view) {
		// setup content buttons
		ui_contentButtons = new HashSet<Button>();
		ui_contentButtons.add((Button) view.findViewById(R.id.picture_button));
		ui_contentButtons.add((Button) view.findViewById(R.id.video_button));
	}

	/*
	 * Add listeners if necessary
	 */
	private void handleEvents(View view) {
		// TODO
		for (Button b : ui_contentButtons) {
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					contentClick(v);
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
