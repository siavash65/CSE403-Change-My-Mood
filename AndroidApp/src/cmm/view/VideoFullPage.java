package cmm.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoFullPage extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webvideo_full);

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		VideoView videoView = (VideoView) findViewById(R.id.webfullvideo);
		MediaController mc = new MediaController(this); 
		mc.setEnabled(true);
		videoView.setMediaController(mc); 
		videoView.setVideoURI(Uri.parse("rtsp://v1.cache4.c.youtube.com/CiILENy73wIaGQn6nXA54I8tyRMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp")); 
		videoView.requestFocus(); videoView.showContextMenu();
		videoView.start();
		
		/*DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = (int)(displaymetrics.widthPixels * 0.48);
		int height = (int)(displaymetrics.heightPixels * 0.48);
		
		String yt_id = "KGk-GL8uNrY";
		WebView fullscreen = (WebView)findViewById(R.id.webfullvideo);
		
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		// Set java script enable
		fullscreen.getSettings().setJavaScriptEnabled(true);
		
		// Do magic to make it work on Android 2.3
		fullscreen.getSettings().setPluginsEnabled(true);
		
		// Some magic again
		fullscreen.getSettings().setUserAgentString(
				"Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");

		// Another type of magic to make it work on Android 4.0
		final Activity myself = this;
		fullscreen.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				myself.setProgress(progress * 1000);
			}
		});

		// Another type of magic to make it work on Android 4.0
		fullscreen.setWebViewClient(new WebViewClient() {
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
				+ "width=\"" + width + "\" height=\"" + height + "\" "
				+ "src=\"http://www.youtube.com/embed/" + yt_id + "?autoplay=1&fs=0\" "
				+ "frameborder=\"0\"></body></html>";
		fullscreen.loadData(playVideo, "text/html", "utf-8");*/
	}
}
