package cmm.view.newview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import cmm.view.R;

//import com.facebook.FacebookActivity;
//import com.facebook.SessionState;

public class FacebookHandler { 
//extends FacebookActivity{
//	public static final String PREFS_NAME = "ula_pref";
//	public static final String REDIRECT = "redirect";
//
//	private static boolean firstTime = true;
//
//	/** Called when the activity is first created. */
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.facebook_sign);
//		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//		boolean isRedirect = settings.getBoolean(REDIRECT, false);
//		if (!firstTime && isRedirect) {
//			this.loginRedirect();
//		}
//
//		// Hack to prevent double load page from FB's first time load
//		if (firstTime) {
//			firstTime = false;
//		}
//	}
//
//	public void login(View view) {
//		this.openSession();
//	}
//
//	public static void logout(Context context) {
//		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
//		SharedPreferences.Editor editor = settings.edit();
//		editor.putBoolean(REDIRECT, false);
//		editor.commit();
//	}
//
//	@Override
//	protected void onSessionStateChange(SessionState state, Exception exception) {
//		// this.centerScreen.setImageResource(R.drawable.loading);
//		// user has either logged in or not ...
//		if (state.isOpened()) {
//			// We need an Editor object to make preference changes.
//			// All objects are from android.context.Context
//			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//			SharedPreferences.Editor editor = settings.edit();
//			editor.putBoolean(REDIRECT, true);
//
//			// Commit the edits!
//			editor.commit();
//
//			this.loginRedirect();
//		} else if (state.isClosed()) {
//			this.finish();
//		}
//	}
//
//	private void loginRedirect() {
//		Toast.makeText(getApplicationContext(),
//				"Signed in successfully!", Toast.LENGTH_SHORT).show();
//
//		finish();
//	}
//	
///*	private static FacebookHandler instance = null;
//	private final static String FACEBOOK_APP_ID = "343378635757581";
//	
//	private Facebook FACEBOOK;
//	private boolean usefb;
//	
//	private FacebookHandler(){
//		FACEBOOK = new Facebook(FACEBOOK_APP_ID);
//		usefb = true;
//	}
//	
//	public static FacebookHandler getInstance(){
//		if(instance == null){
//			instance = new FacebookHandler();
//		}
//		return instance;
//	}
//	
//	private void signin_success(Activity activity, Context context){
//		Intent intent = new Intent(context, MoodPage.class);
//		activity.startActivity(intent);
//	}
//	
//	public void doSignin(final Activity activity, final Context context){
//        if(!FACEBOOK.isSessionValid()) {
//        	FACEBOOK.authorize(activity, new String[] {"email"}, new DialogListener(){
//				@Override
//				public void onComplete(Bundle values){
//					signin_success(activity, context);
//					usefb = true;
//				}
//
//				@Override
//				public void onFacebookError(FacebookError error){}
//
//				@Override
//				public void onError(DialogError e){}
//
//				@Override
//				public void onCancel(){}
//            });
//        }else{
//        	signin_success(activity, context);
//        	usefb = true;
//        }
//	}
//	
//	public void doSignout(final Context context, final Menu menu, 
//										final MenuInflater inflater){
//		new Thread(){
//			@Override public void run(){
//				try {
//					FACEBOOK.logout(context);
//				}catch (MalformedURLException e) {
//					e.printStackTrace();
//					temporary_msg();
//				} catch (IOException e) {
//					e.printStackTrace();
//					temporary_msg();
//				} catch (Exception e){
//					e.printStackTrace();
//					temporary_msg();
//				}
//				usefb = false;
//				menu.clear();
//				inflater.inflate(R.menu.menu_basic, menu);
//			}
//		}.start();
//	}
//	
//	// show temporary message for the functions that are not ready yet.
//    private void temporary_msg(){
//    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//    	builder.setTitle("Message");
//    	builder.setMessage("Sign out got unexpected error! Please try again.");
//    	builder.setNeutralButton("close", new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				
//			}
//		});
//    	builder.show();
//    }
//	
//	public void skipFB(){
//		usefb = false;
//	}
//	
//	public boolean getStatus(){
//		return usefb;
//	}*/
}
