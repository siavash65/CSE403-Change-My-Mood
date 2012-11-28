package cmm.view.newview;

import cmm.view.R;
import cmm.view.newview.navigation.NavigationFragment;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class CmmActivity extends FragmentActivity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.cmm_main);
	    
	    setupComponents();
		handleEvents();
		doLayout();
	}

	/*
	 * Setup the ui components
	 */
	private void setupComponents() {
		// TODO
	}
	
	/*
	 * Add listeners if necessary
	 */
	private void handleEvents() {
		// TODO
	}
	
	/*
	 * Resize layout if necessary
	 */
	private void doLayout() {
		// TODO
		// get an instance of FragmentTransaction from your Activity
	     FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

	     //add a fragment
	     NavigationFragment navigationFragment = new NavigationFragment();
	     fragmentTransaction.add(R.id.navigation_fragment, navigationFragment);
	     fragmentTransaction.commit();
	}
}
