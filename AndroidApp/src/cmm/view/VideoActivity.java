package cmm.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import cmm.model.FacebookHandler;

public class VideoActivity extends Activity {
	private MenuInflater inflater;
	private Menu menu;

	private WebView ui_webplayer;
	
	/** Called when the activity is first created. */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String yt_id = "KGk-GL8uNrY";
		
		// setContentView(R.layout.video_layout);
		setContentView(R.layout.webvideo_layout);
		
		// get web view
		this.ui_webplayer = (WebView) findViewById(R.id.webvideo);
		
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		// Get video size
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;
		int video_width = (int) (0.45 * width);
		int video_height = (int) (video_width * 10 / 16);

		Log.d("ABC", "w = " + video_width + ", h = " + video_height);
		
		// Set java script enable
		ui_webplayer.getSettings().setJavaScriptEnabled(true);
		
		// Do magic to make it work on Android 2.3
		ui_webplayer.getSettings().setPluginsEnabled(true);
		
		// Some magic again
		ui_webplayer
				.getSettings()
				.setUserAgentString(
						"Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");

		// Another type of magic to make it work on Android 4.0
		final Activity myself = this;
		ui_webplayer.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				myself.setProgress(progress * 1000);
			}
		});

		// Another type of magic to make it work on Android 4.0
		ui_webplayer.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(myself, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
		});

		// Some Html
		String playVideo = 
				"<html><body>"
				+ "<iframe class=\"youtube-player\" type=\"text/html\" "
				+ "width=\"" + video_width + "\" height=\"" + video_height + "\" "
				+ "src=\"http://www.youtube.com/embed/" + yt_id + "?autoplay=1&fs=0\" "
				+ "frameborder=\"0\"></body></html>";
		ui_webplayer.loadData(playVideo, "text/html", "utf-8");
		
		/*
		 * VideoView videoView = (VideoView) findViewById(R.id.video_view);
		 * MediaController mc = new MediaController(this); mc.setEnabled(true);
		 * videoView.setMediaController(mc); //videoView.setVideoURI(Uri.parse(
		 * "rtsp://v1.cache4.c.youtube.com/CiILENy73wIaGQn6nXA54I8tyRMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp"
		 * )); videoView.setVideoURI(Uri.parse(
		 * "https://www.youtube.com/watch?v=xVfTUwk4AJI"));
		 * videoView.requestFocus(); videoView.showContextMenu();
		 * videoView.start();
		 */
	}

	public void pluginRedirect(View view) {
		Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( "https://docs.google.com/open?id=0B-wnXVcJVHaXRDRwTmZxQTZSNXc" ) );
	    startActivity( browse );
	}

	public void goFullscreen(View view){
		Intent i = new Intent();
		i.setClass(this, VideoFullPage.class);
		startActivity(i);
	}
	
	// create menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		inflater = getMenuInflater();
		this.menu = menu;

		if (FacebookHandler.getInstance().getStatus()) {
			inflater.inflate(R.menu.menu_login, menu);
		} else {
			inflater.inflate(R.menu.menu_basic, menu);
		}
		return true;
	}

	// handle menu activity
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case R.id.aboutus_menu:
			intent.setClass(this, AboutUs.class);
			startActivity(intent);
			return true;
		case R.id.contactus_menu:
			intent.setClass(this, ContactUs.class);
			startActivity(intent);
			return true;
		case R.id.signout_menu:
			FacebookHandler.getInstance().doSignout(this, menu, inflater);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
