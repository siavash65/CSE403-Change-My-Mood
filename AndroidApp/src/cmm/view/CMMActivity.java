package cmm.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import cmm.model.FacebookHandler;

public class CMMActivity extends Activity{
	//private WebView ui_background;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /*ui_background = (WebView) findViewById(R.id.bg_webview);
        ui_background.loadUrl("file:///android_asset/html/index.html");
        ui_background.getSettings().setJavaScriptEnabled(true);*/
    }
    
	public void skip_signin(View view){
		Intent intent = new Intent(this, MoodPage.class);
		FacebookHandler.getInstance().skipFB();
		startActivity(intent);
	}
	
	public void facebookSignin(View view){
		FacebookHandler.getInstance().doSignin(this, getBaseContext());
	}
}
