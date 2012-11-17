package cmm.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.Toast;
import cmm.model.Content;
import cmm.model.FacebookHandler;
import cmm.model.Mood;
import cmm.model.Rate;
import cmm.model.UrlProvider;

public class VideoActivity extends Activity {
	private MenuInflater inflater;
	private Menu menu;

	private WebView ui_webplayer;
	
	private String vid;
	private int vid_mood_type;
	private int vid_content_type;
	private int video_width;
	private int video_height;
	private String vid_url;
	private Button _thumbup;
	private Button _thumbdown;
	
	/** Called when the activity is first created. */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		video_width = (int) (0.45 * width);
		video_height = (int) (video_width * 10 / 16);

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
		
		Bundle bundle = getIntent().getExtras();
		vid_mood_type = bundle.getInt(MoodPage.MOOD);
		vid_content_type = bundle.getInt(MediaPage.CONTENT);
		
		_thumbup = (Button) findViewById(R.id.VThumbsUp);
		_thumbdown = (Button) findViewById(R.id.VThumbsDown);
		
		new GetVideoTask(this).execute(vid_mood_type, vid_content_type);
	}

	public void pluginRedirect(View view) {
		Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( "https://docs.google.com/open?id=0B-wnXVcJVHaXRDRwTmZxQTZSNXc" ) );
	    startActivity( browse );
	}

	public void goFullscreen(View view){
		Intent i = new Intent();
		i.setClass(this, VideoFullPage.class);
		i.putExtra("URL", vid_url);
		startActivity(i);
	}
	
	public void thumbsUp(View view){
		_thumbup.setEnabled(false);
		_thumbdown.setEnabled(false);
		new RateVideoTask().execute(vid, Rate.THUMBSUP.ordinal() + "");
	}
	
	public void thumbsDown(View view){
		_thumbup.setEnabled(false);
		_thumbdown.setEnabled(false);
		new RateVideoTask().execute(vid, Rate.THUMBSDOWN.ordinal() + "");
	}
	
	public void nextVideo(View view){
		_thumbup.setEnabled(false);
		_thumbdown.setEnabled(false);
		new GetVideoTask(this).execute(vid_mood_type, vid_content_type);
	}
	
	private class GetVideoTask extends AsyncTask<Integer, Integer, String>{
		private VideoActivity va;
		
		public GetVideoTask(VideoActivity vact){
			super();
			va = vact;
		}
		
		@Override
		protected String doInBackground(Integer... arg) {
			try{
				HttpClient client = new DefaultHttpClient();
				String vd_url = UrlProvider.getVideoUrl(Mood.fromInt(arg[0]), Content.fromInt(arg[1]));
				
				HttpGet hg = new HttpGet(vd_url);
				HttpResponse hr = client.execute(hg);
				HttpEntity he = hr.getEntity();
				
				if(he != null){
					String result = EntityUtils.toString(he);
					
					JSONObject jresult = new JSONObject(result);
					va.vid = jresult.getString("mid");
					vid_url = jresult.getString("url");
					return vid_url;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String str){
			String link = str.split("=", 2)[1];
			
			// Some Html
			String playVideo = 
				"<html><body>"
				+ "<iframe class=\"youtube-player\" type=\"text/html\" "
				+ "width=\"" + video_width + "\" height=\"" + video_height + "\" "
				+ "src=\"http://www.youtube.com/embed/" + link + "\" "
				+ "frameborder=\"0\"></body></html>";
			ui_webplayer.loadData(playVideo, "text/html", "utf-8");
			
			if(!_thumbup.isEnabled()){
				_thumbup.setEnabled(true);
			}
			
			if(!_thumbdown.isEnabled()){
				_thumbdown.setEnabled(true);
			}
		}
	}
	
	private class RateVideoTask extends AsyncTask<String, Integer, Boolean> {
		// 0th = mid 1st = rank
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				HttpClient client = new DefaultHttpClient();
				String postURL = UrlProvider.getRankUrl();
				HttpPost post = new HttpPost(postURL);
				List<NameValuePair> post_params = new ArrayList<NameValuePair>(
						2);
				post_params.add(new BasicNameValuePair(UrlProvider.MID,
						params[0]));
				post_params.add(new BasicNameValuePair(UrlProvider.RATE,
						params[1]));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(
						post_params, HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();
				if (resEntity != null) {
					String rsp = EntityUtils.toString(resEntity);
					Log.i("RESPONSE", rsp);
					JSONObject json = new JSONObject(rsp);

					if (json.has(UrlProvider.SUCCESS)) {
						return true;
					}
					return false;
				}
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean s) {
			String msg = s ? getResources()
					.getString(R.string.rate_success_msg) : getResources()
					.getString(R.string.rate_fail_msg);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
					.show();
		}
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
