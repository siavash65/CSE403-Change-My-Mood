package cmm.view;

import com.facebook.android.*;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CMMActivity extends Activity{
	private final String ABOUTUS = "About Us";
	private final boolean SKIP = true;
	private final String CONTACTUS = "Contact Us";
	private final String SIGNIN = "Signin";
    
	private SharedPreferences mPrefs;
	private Facebook facebook;
	private AsyncFacebookRunner mAysncRunner;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        facebook = new Facebook("343378635757581");
        mAysncRunner = new AsyncFacebookRunner(facebook);
    }
    
	public void skip_signin(View view){
		Intent intent = new Intent(this, MoodPage.class);
		Bundle fbinfo = new Bundle();
		fbinfo.putBoolean(SIGNIN, SKIP);
		intent.putExtras(fbinfo);
		Log.d("Main", "skip siginin");
		startActivity(intent);
	}
	
	private void login_success(){
		Intent intent = new Intent(getBaseContext(), MoodPage.class);
		Bundle fbinfo = new Bundle();
		fbinfo.putString("provider", "facebook");
		//fbinfo.putString("uid", facebook. user.getId());
		//fbinfo.putString("name", user.getName());
		fbinfo.putBoolean(SIGNIN, !SKIP);
		intent.putExtras(fbinfo);
		startActivity(intent);
	}
	
	public void facebookSignin(View view){
		/*
         * Get existing access_token if any
         */
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null && expires != 0) {
            facebook.setAccessToken(access_token);
            facebook.setAccessExpires(expires);
            login_success();
        }
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!facebook.isSessionValid()) {
            facebook.authorize(this, new String[] {"email"}, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
				    SharedPreferences.Editor editor = mPrefs.edit();
				    editor.putString("access_token", facebook.getAccessToken());
				    editor.putLong("access_expires", facebook.getAccessExpires());
				    editor.commit();
				    
				    login_success();
				}
				
				@Override
				public void onFacebookError(FacebookError error) {}
				
				@Override
				public void onError(DialogError e) {}
				
				@Override
				public void onCancel() {}
            });
        }
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
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