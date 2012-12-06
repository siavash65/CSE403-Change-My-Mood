package cmm.view.newview;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import cmm.view.R;

import com.facebook.FacebookActivity;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

public class CmmFacebookActivity extends FacebookActivity {
	public static boolean isSignedIn = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_logging);
	}

	public void login(View view) {
		this.openSession();
	}

	@Override
	protected void onSessionStateChange(SessionState state, Exception exception) {
		// user has either logged in or not ...
		if (state.isOpened()) {
			this.isSignedIn = true;
			// make request to the /me API
			Request request = Request.newMeRequest(this.getSession(),
					new Request.GraphUserCallback() {
						// callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							if (user != null) {
								// TextView welcome = (TextView)
								// findViewById(R.id.welcome);
								// welcome.setText("Hello " + user.getName() +
								// "!");
								Toast.makeText(getApplicationContext(),
										"Hello " + user.getName(),
										Toast.LENGTH_SHORT).show();
								
							}
						}
					});
			Request.executeBatchAsync(request);
		} else if (state.isClosed()) {
			Toast.makeText(getApplicationContext(),
					"Logged out",
					Toast.LENGTH_SHORT).show();
		}
	}
}
