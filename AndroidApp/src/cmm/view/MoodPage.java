package cmm.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cmm.model.Mood;

import com.facebook.android.Facebook;

public class MoodPage extends Activity{
	private static int signin;
	private final String SIGNIN ="Sign in";
	public static final String MOOD = "Mood";
	private Intent intent;
	private MenuInflater inflater;
	private Menu menu;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moodpage);
       
        //we can change later
        Bundle bundle = getIntent().getExtras();
        signin = bundle.getInt(SIGNIN);
        Log.d("Extra data", signin + "");
        
        intent = new Intent();
        intent.setClass(this, MediaPage.class);
    }

    public void clickLaugh(View view){
    	intent.putExtra(MOOD, Mood.HUMOROUS.ordinal());
    	startActivity(intent);
    }
    
    public void clickPump(View view){
    	//intent.putExtra(MOOD, Mood.ENERVATE);
    	//startActivity(intent);
    	temporary_msg();
    }
    
    public void clickInspire(View view){
    	//intent.putExtra(MOOD, Mood.INSPIRE);
    	//startActivity(intent);
    	temporary_msg();
    }
    
    public void clickRomance(View view){
    	//intent.putExtra(MOOD, Mood.ROMANTIC);
    	//startActivity(intent);
    	temporary_msg();
    }
    
    // show temporary message for the functions that are not ready yet.
    private void temporary_msg(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Message");
    	builder.setMessage(R.string.notready);
    	builder.setNeutralButton("close", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
    	builder.show();
    }

    // create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	inflater = getMenuInflater();
    	this.menu = menu;
    	
    	if(Facebook.TOKEN != null){
    		inflater.inflate(R.menu.menu_login, menu);
    	}else{
    		inflater.inflate(R.menu.menu_basic, menu);
    	}
        return true;
    }
    
    // handle menu activity 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = new Intent();
    	switch(item.getItemId()){
    		case R.id.aboutus_menu:
	    		intent.setClass(this, AboutUs.class);
	    		startActivity(intent);
	    		return true;
    		case R.id.contactus_menu:
	    		intent.setClass(this, ContactUs.class);
	    		startActivity(intent);
	    		return true;
    		case R.id.signout_menu:
    			//menu.clear();
				//inflater.inflate(R.menu.menu_basic, menu);
    			temporary_msg();
				return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
    	}
    }
}
