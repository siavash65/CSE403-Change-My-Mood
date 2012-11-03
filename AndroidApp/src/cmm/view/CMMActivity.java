package cmm.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class CMMActivity extends Activity{
	private final boolean SKIP = true;
	private final String SIGNIN = "Signin";
	public final static String FACEBOOK_APP_ID = "343378635757581";
    
	private SharedPreferences mPrefs;
	public static Facebook FACEBOOK;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        FACEBOOK = new Facebook(FACEBOOK_APP_ID);
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
        	FACEBOOK.setAccessToken(access_token);
        	FACEBOOK.setAccessExpires(expires);
            login_success();
        }
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!FACEBOOK.isSessionValid()) {
        	FACEBOOK.authorize(this, new String[] {"email"}, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
				    SharedPreferences.Editor editor = mPrefs.edit();
				    editor.putString("access_token", FACEBOOK.getAccessToken());
				    editor.putLong("access_expires", FACEBOOK.getAccessExpires());
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
        FACEBOOK.authorizeCallback(requestCode, resultCode, data);
    }
}