package cmm.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookHandler extends Activity{
	private static FacebookHandler instance = null;
	private final static String FACEBOOK_APP_ID = "343378635757581";
	
	private Facebook FACEBOOK;
	private boolean usefb;
	private boolean signouterr;
	
	private FacebookHandler(){
		FACEBOOK = new Facebook(FACEBOOK_APP_ID);
		usefb = true;
		signouterr = false;
	}
	
	public static FacebookHandler getInstance(){
		if(instance == null){
			instance = new FacebookHandler();
		}
		return instance;
	}
	
	public void doSignin(final Activity activity, final Context context){
        if(!FACEBOOK.isSessionValid()) {
        	FACEBOOK.authorize(activity, new String[] {"email"}, new DialogListener(){
				@Override
				public void onComplete(Bundle values){
					Toast.makeText(context, "Signed in!", Toast.LENGTH_SHORT).show();
					usefb = true;
				}

				@Override
				public void onFacebookError(FacebookError error){
					error.printStackTrace();
					temporary_msg(context);
				}

				@Override
				public void onError(DialogError e){
					e.printStackTrace();
					temporary_msg(context);
				}

				@Override
				public void onCancel(){}
            });
        }else{
        	Toast.makeText(context, "Signed in!", Toast.LENGTH_SHORT).show();
        	usefb = true;
        }
	}
	
	//public void doSignout(final Context context, final Menu menu, 
										//final MenuInflater inflater){
	public void doSignout(final Context context){
		if(usefb && FACEBOOK.isSessionValid()){
			signouterr = false;
			AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(FACEBOOK);
			mAsyncRunner.logout(context, new RequestListener(){
				@Override
				public void onComplete(String response, Object state) {
					usefb = false;
				}

				@Override
				public void onIOException(IOException e,
						Object state) {
					e.printStackTrace();
					temporary_msg(context);
					signouterr = true;
				}

				@Override
				public void onFileNotFoundException(
						FileNotFoundException e, Object state) {
					e.printStackTrace();
					temporary_msg(context);
					signouterr = true;
				}

				@Override
				public void onMalformedURLException(
						MalformedURLException e, Object state) {
					e.printStackTrace();
					temporary_msg(context);
					signouterr = true;
				}

				@Override
				public void onFacebookError(FacebookError e,
						Object state) {
					e.printStackTrace();
					temporary_msg(context);
					signouterr = true;
				}
			});
			
			if(!signouterr){
				Toast.makeText(context, "Signed out!", Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(context, "Please sign in first", Toast.LENGTH_SHORT).show();
		}
	}
	
	// show temporary message for the functions that are not ready yet.
    private void temporary_msg(Context context){
    	Toast.makeText(context, "Sign in/out got unexpected error! Please try again.", Toast.LENGTH_SHORT).show();
    }
	
	public void skipFB(){
		usefb = false;
	}
	
	public boolean getStatus(){
		return usefb;
	}
	
	/*
	private void signin_success(Activity activity, Context context){
		Intent intent = new Intent(context, MoodPage.class);
		activity.startActivity(intent);
	}*/
	
	/*
	public static final String PREFS_NAME = "ula_pref";
	public static final String REDIRECT = "redirect";

	private static boolean firstTime = true;

	/** Called when the activity is first created. */
	/*
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_sign);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean isRedirect = settings.getBoolean(REDIRECT, false);
		if (!firstTime && isRedirect) {
			this.loginRedirect();
		}

		if (firstTime) {
			firstTime = false;
		}
	}

	public void login(View view) {
		this.openSession();
	}

	public static void logout(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(REDIRECT, false);
		editor.commit();
	}

	@Override
	protected void onSessionStateChange(SessionState state, Exception exception) {
		if (state.isOpened()) {
			// We need an Editor object to make preference changes.
			// All objects are from android.context.Context
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(REDIRECT, true);

			editor.commit();

			this.loginRedirect();
		} else if (state.isClosed()) {
			this.finish();
		}
	}

	private void loginRedirect() {
		Toast.makeText(getApplicationContext(),
				"Signed in successfully!", Toast.LENGTH_SHORT).show();

		finish();
	}*/
}
