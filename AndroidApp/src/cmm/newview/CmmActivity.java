package cmm.newview;

import cmm.view.R;
import android.app.Activity;
import android.os.Bundle;

public class CmmActivity extends Activity {

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
	}
}
