package cmm.view.newview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import cmm.view.R;

import com.facebook.FacebookActivity;
import com.facebook.SessionState;

public class FacebookHandler extends FacebookActivity{
	public static final String PREFS_NAME = "ula_pref";
	public static final String REDIRECT = "redirect";

	private static boolean firstTime = true;

	/** Called when the activity is first created. */
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
	}
}
