package cmm.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cmm.model.Content;
import cmm.model.FacebookHandler;

public class MediaPage extends Activity{
	public static String CONTENT = "Content";
	private Intent intent;
	private MenuInflater inflater;
	private Menu menu;
	private int mood_type;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediapage);
        
        Bundle bundle = getIntent().getExtras();
        mood_type = bundle.getInt(MoodPage.MOOD);
        intent = new Intent(this, PictureActivity.class);
    }
    
    public void gotoPicture(View view) {
    	intent.putExtra(MoodPage.MOOD, mood_type);
    	intent.putExtra(CONTENT, Content.PICTURE.ordinal());
    	startActivity(intent);
    }
    
    public void gotoVideo(View view) {
    	temporary_msg();
    }
    
    public void gotoAudio(View view){
    	temporary_msg();
    }
    
    public void gotoText(View view){
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
    	
    	if(FacebookHandler.getInstance().getStatus()){
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
    			FacebookHandler.getInstance().doSignout(this, menu, inflater);
    			return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
    	}
    }
}
