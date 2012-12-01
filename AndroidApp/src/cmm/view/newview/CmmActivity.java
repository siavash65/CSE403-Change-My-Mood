package cmm.view.newview;

import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import cmm.model.Content;
import cmm.model.Mood;
import cmm.model.UrlProvider;
import cmm.view.R;
import cmm.view.VideoActivity;
import cmm.view.newview.contentdisplay.ContentDisplayFragment;
import cmm.view.newview.navigation.NavigationFragment;

public class CmmActivity extends FragmentActivity {
	/* debug log string */
	private static final String TAG = "CmmActivity";

	private static final double CONTENT_W_OVER_H = 16.0 / 9.0;

	/* Model Objects */
	private String cur_mid;
	private String cur_url;

	/* UI objects */
	private Point ui_dimension;
	private LinearLayout ui_contentLayout;
	private ContentDisplayFragment contentFragment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.cmm_main);

		setupComponents();
		handleEvents();
		doLayout();
	}

	/*
	 * Setup the ui components
	 */
	private void setupComponents() {
		// Find screen size
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		size.x = display.getWidth();
		size.y = display.getHeight();
		Log.d("CmmActivity", "x: " + size.x + " ,y:" + size.y);

		ui_dimension = size;

		// set content layout
		ui_contentLayout = (LinearLayout) this
				.findViewById(R.id.content_layout);
	}

	/*
	 * Add listeners if necessary
	 */
	private void handleEvents() {
		// TODO
	}

	/*
	 * Resize layout if necessary
	 */
	private void doLayout() {
		// TODO
		// Hard set layout
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		// Auto adjust content layout
		// Gets the layout params that will allow you to resize the layout
		android.view.ViewGroup.LayoutParams params = (android.view.ViewGroup.LayoutParams) ui_contentLayout
				.getLayoutParams();
		// Changes the height and width to the specified *pixels*
		params.height = (int) (1.0 * ui_dimension.x / CONTENT_W_OVER_H);

		// get an instance of FragmentTransaction from your Activity
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();

		// add navigation fragment
		NavigationFragment navigationFragment = new NavigationFragment(this);
		fragmentTransaction.add(R.id.navigation_fragment, navigationFragment);

		// add content fragment
		this.contentFragment = ContentDisplayFragment.getInstance(this);
		fragmentTransaction.add(R.id.content_fragment, contentFragment);

		fragmentTransaction.commit();
	}

	public void displayImage(Mood mood) {
		new GetPictureTask(this).execute(mood.ordinal(), Content.PICTURE.ordinal());
	}

	public void displayVideo(Mood mood) {
		new GetVideoTask(this).execute(mood.ordinal(), Content.VIDEO.ordinal());
	}

	private class GetVideoTask extends AsyncTask<Integer, Integer, String> {
		private CmmActivity va;
		private String yid;

		public GetVideoTask(CmmActivity vact) {
			super();
			va = vact;
		}

		@Override
		protected String doInBackground(Integer... arg) {
			try {
				HttpClient client = new DefaultHttpClient();
				String vd_url = UrlProvider.getVideoUrl(Mood.fromInt(arg[0]),
						Content.fromInt(arg[1]));

				HttpGet hg = new HttpGet(vd_url);
				HttpResponse hr = client.execute(hg);
				HttpEntity he = hr.getEntity();

				if (he != null) {
					String result = EntityUtils.toString(he);

					JSONObject jresult = new JSONObject(result);
					va.cur_mid = jresult.getString("mid");
					va.cur_url = jresult.getString("url");
					return va.cur_url;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String str) {
			contentFragment.displayVideo(str);
		}
	}

	/**
	 * Android 4.X Need to run networking code on another thread
	 * 
	 * @author hunlan
	 * 
	 */
	private class GetPictureTask extends AsyncTask<Integer, Integer, Drawable> {
		private CmmActivity myactivity;

		public GetPictureTask(CmmActivity myactivity) {
			super();
			this.myactivity = myactivity;
		}

		// 0th = mood, 1st = content
		@Override
		protected Drawable doInBackground(Integer... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				String url_str = UrlProvider.getPictureUrl(
						Mood.fromInt(params[0]), Content.fromInt(params[1]));

				Log.d(TAG, "url: " + url_str);

				HttpGet get = new HttpGet(url_str);

				HttpResponse responseGet = client.execute(get);

				HttpEntity resEntityGet = responseGet.getEntity();

				if (resEntityGet != null) {
					// do something with the response
					String rsp = EntityUtils.toString(resEntityGet);
					Log.i("GET RESPONSE", rsp);

					JSONObject json = new JSONObject(rsp);

					String link = json.getString("url");
					Log.d(TAG, "link: " + link);
					URL url = new URL(link);
					InputStream is = (InputStream) url.getContent();
					Drawable image = Drawable.createFromStream(is, "src");

					// get mid and store it
					this.myactivity.cur_mid = json.getString("mid");
					this.myactivity.cur_url = link;

					return image;
				} else {
					Log.d(TAG, "resEntityGet is null");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Drawable result) {
			contentFragment.displayImage(result);
			// Enable Rating here
		}
	}
}
