package cmm.view;

import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cmm.model.Content;
import cmm.model.Mood;
import cmm.model.UrlProvider;

public class PictureActivity extends Activity {
	/* debug log string */
	private static final String TAG = "PictureActivity";

	// g_ = gui components
	private Button g_up;
	private Button g_down;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.picture_layout);

		fixUI();

		// TODO IS THIS GOOD??? OR SHOULD I PASS INFO FROM PREVIOUS ACTIVITY?
		new GetPictureTask().execute(Mood.HUMOROUS.ordinal(),
				Content.PICTURE.ordinal());
	}

	public void changePicture(View view) {
		g_up.setEnabled(false);
		g_down.setEnabled(false);
		new GetPictureTask().execute(Mood.HUMOROUS.ordinal(),
				Content.PICTURE.ordinal());
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

	private class GetPictureTask extends AsyncTask<Integer, Integer, Drawable> {

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
			if(!g_up.isEnabled()) {
				g_up.setEnabled(true);
			}
			
			if(!g_down.isEnabled()) {
				g_down.setEnabled(true);
			}
		}

	}
}
