package cmm.model;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cmm.view.MoodPage;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class FacebookHandler extends Activity{
	private static FacebookHandler instance = null;
	public Facebook FACEBOOK;
	private final static String FACEBOOK_APP_ID = "343378635757581";
	public final static boolean SKIP = true;
	public final static String SIGNIN = "Signin";
	
	private FacebookHandler(){
		FACEBOOK = new Facebook(FACEBOOK_APP_ID);
	}
	
	public static FacebookHandler getInstance(){
		if(instance == null){
			instance = new FacebookHandler();
		}
		return instance;
	}
	
	private void signin_success(Activity activity, Context context){
		Intent intent = new Intent(context, MoodPage.class);
		Bundle fbinfo = new Bundle();
		fbinfo.putString("provider", "facebook");
		//fbinfo.putString("uid", facebook. user.getId());
		//fbinfo.putString("name", user.getName());
		//fbinfo.putBoolean(SIGNIN, !SKIP);
		intent.putExtras(fbinfo);
		activity.startActivity(intent);
	}
	
	public void doSignin(final Activity activity, final Context context){
        if(!FACEBOOK.isSessionValid()) {
        	FACEBOOK.authorize(activity, new String[] {"email"}, new DialogListener(){
				@Override
				public void onComplete(Bundle values){
					signin_success(activity, context);
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
        }
	}
	
	public void doSignout(Context context){
		try {
			FACEBOOK.logout(context);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
