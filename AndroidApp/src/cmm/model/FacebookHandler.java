package cmm.model;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	
	public void doSignout(final Context context, final Menu menu, 
										final MenuInflater inflater){
		new Thread(){
			@Override public void run(){
				try {
					FACEBOOK.logout(context);
				}catch (MalformedURLException e) {
					e.printStackTrace();
					temporary_msg();
				} catch (IOException e) {
					e.printStackTrace();
					temporary_msg();
				} catch (Exception e){
					e.printStackTrace();
					temporary_msg();
				}
				usefb = false;
				menu.clear();
				inflater.inflate(R.menu.menu_basic, menu);
			}
		}.start();
	}
	
	// show temporary message for the functions that are not ready yet.
    private void temporary_msg(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Message");
    	builder.setMessage("Sign out got unexpected error! Please try again.");
    	builder.setNeutralButton("close", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
    	builder.show();
    }
	
	public void skipFB(){
		usefb = false;
	}
	
	public boolean getStatus(){
		return usefb;
	}
}
