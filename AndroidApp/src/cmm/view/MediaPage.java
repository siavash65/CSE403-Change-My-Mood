package cmm.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
// For Code Review
public class MediaPage extends Activity{
	private final String ABOUTUS = "About Us";
	private final String CONTACTUS = "Contact Us";
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediapage);
    }
    
    public void gotoPicture(View view) {
    	Intent intent = new Intent(this, PictureActivity.class);
    	startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(ABOUTUS);
        menu.add(CONTACTUS);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = new Intent();
    	if(item.getTitle().equals(ABOUTUS)) {
    		intent.setClass(this, AboutUs.class);
    		startActivity(intent);
    		return true;
    	} else if (item.getTitle().equals(CONTACTUS)) {
    		intent.setClass(this, ContactUs.class);
    		startActivity(intent);
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
}
