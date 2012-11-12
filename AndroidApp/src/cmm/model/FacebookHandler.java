package cmm.model;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import cmm.view.MoodPage;
import cmm.view.R;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookHandler extends Activity{
	private static FacebookHandler instance = null;
	private final static String FACEBOOK_APP_ID = "343378635757581";
	
	private Facebook FACEBOOK;
	private boolean usefb;
	
	private FacebookHandler(){
		FACEBOOK = new Facebook(FACEBOOK_APP_ID);
		usefb = true;
	}
	
	public static FacebookHandler getInstance(){
		if(instance == null){
			instance = new FacebookHandler();
		}
		return instance;
	}
	
	private void signin_success(Activity activity, Context context){
		Intent intent = new Intent(context, MoodPage.class);
		activity.startActivity(intent);
	}
	
	public void doSignin(final Activity activity, final Context context){
        if(!FACEBOOK.isSessionValid()) {
        	FACEBOOK.authorize(activity, new String[] {"email"}, new DialogListener(){
				@Override
				public void onComplete(Bundle values){
					signin_success(activity, context);
					usefb = true;
				}

				@Override
				public void onFacebookError(FacebookError error){}

				@Override
				public void onError(DialogError e){}

				@Override
				public void onCancel(){}
            });
        }else{
        	signin_success(activity, context);
        	usefb = true;
        }
	}
	
	public void doSignout(Context context, Menu menu, MenuInflater inflater){
		try {
			FACEBOOK.logout(context);
			usefb = false;
			menu.clear();
			inflater.inflate(R.menu.menu_basic, menu);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void skipFB(){
		usefb = false;
	}
	
	public boolean getStatus(){
		return usefb;
	}
}
