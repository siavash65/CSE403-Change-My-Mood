package cmm.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cmm.view.newview.CmmActivity;

public class SplashActivity extends Activity {
	protected int _splashTime = 5000;
	
	private Thread splashThread;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.splash_layout);
	    
	    final SplashActivity splashScreen = this;
	    
	    splashThread = new Thread() {
	    	@Override
	    	public void run() {
	    		try {
	    			synchronized(this) {
	    				wait(_splashTime);
	    			}
	    		} catch (InterruptedException e) {}
	    		finally {
	    			finish();
	    			
	    			Intent intent = new Intent();
	    			intent.setClass(splashScreen, CmmActivity.class);
	    			startActivity(intent);
	    		}
	    	}
	    };
	    
	    splashThread.start();
	}
	
	@Override
	public void onBackPressed() {
		
	}
}
