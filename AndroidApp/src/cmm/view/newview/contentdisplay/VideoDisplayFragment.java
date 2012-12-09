package cmm.view.newview.contentdisplay;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import cmm.view.R;

/**
 * This class handles displaying of video
 * @author hunlan
 *
 */
public class VideoDisplayFragment extends Fragment {
	private static final String TAG = "VideoDisplayFragment";

	private static VideoDisplayFragment instance;

	private View ui_view;
	private WebView ui_webplayer;
	private String yid;

	private Activity activity;

	/**
	 * Singleton
	 * @param activity
	 * @return
	 */
	public static VideoDisplayFragment getInstance(Activity activity) {
		if (instance == null) {
			instance = new VideoDisplayFragment(activity);
		}
		return instance;
	}

	private VideoDisplayFragment(Activity activity) {
		super();
		this.activity = activity;
	}

	/**
	 * On startup
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.video_fragment, container, false);
		this.ui_view = view;
		this.ui_webplayer = (WebView) view.findViewById(R.id.video_content);
		Log.d(TAG, "ui_webplayer == null: " + (ui_webplayer == null));
		doVideoSettings();
		return view;
	}

	// Displaying youtube video from youtube url
	// precondition: url is not null
	public void displayVideo(String url) {
		if (url == null) {
			Log.d(TAG, "something wrong, url is null");
			return;
		}
		
		Log.d(TAG, "displaying url: " + url);
		this.yid = url.split("=", 2)[1];

		ui_webplayer.setVisibility(View.INVISIBLE);
		ui_webplayer.loadUrl("file:///android_asset/html/index.html");
		ui_webplayer.setWebViewClient(new WebViewClient() {
			public void onPageFinished(WebView view, String url) {
				ui_webplayer.loadUrl("javascript:changesrc('" + yid + "')");
				ui_webplayer.setVisibility(View.VISIBLE);
			}
		});
	}

	// Enable webview
	public void enable() {
		ui_view.setVisibility(View.VISIBLE);
	}

	// Disable webview and stop the video from playing
	public void disable() {
		if (ui_view != null) {
			ui_view.setVisibility(View.GONE);
		}
		if (ui_webplayer != null) {
			ui_webplayer.loadUrl("javascript:close()");
			ui_webplayer.loadData("", "text/html", "utf-8");
		}
	}

	// Bunch of crazy stuff to make youtube work
	private void doVideoSettings() {
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
		final Activity myself = activity;
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

		ui_webplayer.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		});
	}
}
