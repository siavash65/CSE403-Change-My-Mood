package cmm.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import cmm.model.FacebookHandler;

public class CMMActivity extends Activity{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
	public void skip_signin(View view){
		Intent intent = new Intent(this, MoodPage.class);
		Bundle fbinfo = new Bundle();
		fbinfo.putBoolean(FacebookHandler.SIGNIN, FacebookHandler.SKIP);
		intent.putExtras(fbinfo);
		Log.d("Main", "skip siginin");
		startActivity(intent);
	}
	
	public void facebookSignin(View view){
		FacebookHandler.getInstance().doSignin(this, getBaseContext());
	}
}
