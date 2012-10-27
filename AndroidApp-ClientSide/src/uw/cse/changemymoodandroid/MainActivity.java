package uw.cse.changemymoodandroid;

import com.facebook.FacebookActivity;
import com.facebook.GraphUser;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.SessionState;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends FacebookActivity {
	private ImageView centerScreen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.centerScreen = (ImageView) findViewById(R.id.center_screen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void login(View view) {
		this.openSession();
	}

	public void skip(View view) {
		// make request to the /me API
		Intent intent = new Intent(getBaseContext(), MoodSelectionActivity.class);
		Bundle fbinfo = new Bundle();
		//TODO Make this an enum please
		fbinfo.putBoolean("login", false);
		intent.putExtras(fbinfo);
		Log.d("MAIN", "skipped");
		startActivity(intent);
	}
	
	@Override
	protected void onSessionStateChange(SessionState state, Exception exception) {
		//this.centerScreen.setImageResource(R.drawable.loading);
		// user has either logged in or not ...
		if (state.isOpened()) {
			// TODO bad style to put text here
			Toast.makeText(getApplicationContext(), "Successfully logged in",
					Toast.LENGTH_SHORT).show();
			
			Request request = Request.newMeRequest(this.getSession(),
					new Request.GraphUserCallback() {
						// callback after Graph API response with user object
						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							if (user != null) {
								// make request to the /me API
								Intent intent = new Intent(getBaseContext(), MoodSelectionActivity.class);
								Bundle fbinfo = new Bundle();
								//TODO Make this an enum please
								fbinfo.putString("provider", "facebook");
								fbinfo.putString("uid", user.getId());
								fbinfo.putString("name", user.getName());
								fbinfo.putBoolean("login", true);
								intent.putExtras(fbinfo);
								startActivity(intent);
								// TextView welcome = (TextView)
								// findViewById(R.id.welcome);
								// welcome.setText("Hello " + user.getName() +
								// "!");
							}
						}
					});
			Request.executeBatchAsync(request);
		}
	}
}
