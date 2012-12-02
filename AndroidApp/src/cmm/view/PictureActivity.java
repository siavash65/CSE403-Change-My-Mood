package cmm.view;

import java.io.InputStream;
import java.net.URL;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cmm.model.Content;
import cmm.model.Mood;
import cmm.model.Rate;
import cmm.model.UrlProvider;
import cmm.view.newview.FacebookHandler;

public class PictureActivity extends Activity {
	/* debug log string */
	private static final String TAG = "PictureActivity";

	private String cur_mid;
	private int cur_mood_type;
	private int cur_content_type;

	// g_ = gui components
	private Button g_up;
	private Button g_down;
	
	private MenuInflater inflater;
	private Menu menu;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.picture_layout);

		fixUI();
		
		// gets mood type and content type from last page.
		Bundle bundle = getIntent().getExtras();
		cur_mood_type = bundle.getInt(MoodPage.MOOD);
		cur_content_type = bundle.getInt(MediaPage.CONTENT);
		
		Button previous = (Button) findViewById(R.id.Previous);
		previous.setEnabled(false);
		
		new GetPictureTask(this).execute(cur_mood_type, cur_content_type);
	}

	public void thumbsUp(View view) {
		g_up.setEnabled(false);
		g_down.setEnabled(false);
		new RatePictureTask().execute(this.cur_mid, Rate.THUMBSUP.ordinal() + "");
	}
	
	public void thumbsDown(View view) {
		g_up.setEnabled(false);
		g_down.setEnabled(false);
		new RatePictureTask().execute(this.cur_mid, Rate.THUMBSDOWN.ordinal() + "");
	}

	public void changePicture(View view) {
		g_up.setEnabled(false);
		g_down.setEnabled(false);
		new GetPictureTask(this).execute(cur_mood_type, cur_content_type);
	}

	private void fixUI() {
		g_up = (Button) findViewById(R.id.ThumbsUp);
		g_down = (Button) findViewById(R.id.ThumbsDown);

		g_up.setEnabled(false);
		g_down.setEnabled(false);

		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int width = display.getWidth();

		LinearLayout lay = (LinearLayout) findViewById(R.id.ImageLayout);
		lay.getLayoutParams().height = width;
	}

	private class RatePictureTask extends AsyncTask<String, Integer, Boolean> {

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

	/**
	 * Need this for 4. + api
	 * 
	 * @author hunlan
	 * 
	 */
	private class GetPictureTask extends AsyncTask<Integer, Integer, Drawable> {
		private PictureActivity myactivity;

		public GetPictureTask(PictureActivity myactivity) {
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

					// get mid and store it
					this.myactivity.cur_mid = json.getString("mid");

					String link = json.getString("url");
					Log.d(TAG, "link: " + link);
					URL url = new URL(link);
					InputStream is = (InputStream) url.getContent();
					Drawable image = Drawable.createFromStream(is, "src");
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
			ImageView imgView = (ImageView) findViewById(R.id.picture);
			imgView.setImageDrawable(result);
			if (!g_up.isEnabled()) {
				g_up.setEnabled(true);
			}

			if (!g_down.isEnabled()) {
				g_down.setEnabled(true);
			}
		}
	}
	
	// create menu
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	inflater = getMenuInflater();
    	this.menu = menu;
    	
    	/*if(FacebookHandler.getInstance().getStatus()){
    		inflater.inflate(R.menu.menu_login, menu);
    	}else{
    		inflater.inflate(R.menu.menu_basic, menu);
    	}*/
        return true;
    }
    
	// handle menu activity 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = new Intent();
    	switch(item.getItemId()){
    		case R.id.aboutus_menu:
	    		intent.setClass(this, AboutUs.class);
	    		startActivity(intent);
	    		return true;
    		case R.id.contactus_menu:
	    		intent.setClass(this, ContactUs.class);
	    		startActivity(intent);
	    		return true;
    		case R.id.signout_menu:
    			//FacebookHandler.getInstance().doSignout(this, menu, inflater);
				return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
    	}
    }
}
