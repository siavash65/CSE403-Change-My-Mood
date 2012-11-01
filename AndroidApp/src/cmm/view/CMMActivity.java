package cmm.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CMMActivity extends Activity implements OnClickListener {
	private Button noSignin;
	private Button withSignin;
	private final String ABOUTUS = "About Us";
	private final String CONTACTUS = "Contact Us";
	private final String SIGNIN ="Sign in";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        noSignin = (Button) findViewById(R.id.WithoutLogin);
        withSignin = (Button) findViewById(R.id.FacebookLogin);
        
        noSignin.setOnClickListener(this);
        withSignin.setOnClickListener(this);
    }
    
    public void onClick(View v) {
    	Intent intent = new Intent();
    	intent.setClass(this, MoodPage.class);
    	if(v.getId()==R.id.WithoutLogin){
    		intent.putExtra(SIGNIN, 0);
    	} else if(v.getId()==R.id.FacebookLogin){
    		intent.putExtra(SIGNIN, 1);
    	}
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