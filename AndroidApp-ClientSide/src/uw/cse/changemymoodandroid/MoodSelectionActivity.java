package uw.cse.changemymoodandroid;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MoodSelectionActivity extends Activity {
	private boolean isLoggedIn;
	private final String TAG = "MoodSelectionActivity";
	private final String GET_URL = "http://uwcse403changemymoodv0.herokuapp.com/api/flickr/";

	  
	  
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mood_page);
		
		this.isLoggedIn = getIntent().getExtras().getBoolean("login");

		if (this.isLoggedIn) {
			// TODO bad style to put text here
			String name = getIntent().getExtras().getString("name");
			Toast.makeText(getApplicationContext(), "Welcome " + name,
					Toast.LENGTH_SHORT).show();

		} else {
			Toast.makeText(getApplicationContext(), "skipped logged in",
					Toast.LENGTH_SHORT).show();
		}
	}

	private class DownloadPicturesTask extends AsyncTask<String, Integer, Drawable> {

		@Override
		protected Drawable doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(arg0[0]);
				HttpResponse responseGet = client.execute(get);
				HttpEntity resEntityGet = responseGet.getEntity();
				if (resEntityGet != null) {
					// do something with the response
					String rsp = EntityUtils.toString(resEntityGet);
					Log.i("GET RESPONSE", rsp);

					// THIS IS FUCKING RETARTED
					rsp = rsp.replaceAll("\\\\", "");
					rsp = rsp.replaceAll("\"", "\\\"");
					rsp = rsp.substring(1, rsp.length() - 1);

					JSONObject json = new JSONObject(rsp.toString());
					String link = json.getString("url");
					Log.d(TAG, "link: " + link);
					URL url = new URL(link);
					InputStream is = (InputStream) url.getContent();
					Drawable image = Drawable.createFromStream(is, "src");
					return image;
				} else {
					Log.d("TEST", "resEntityGet is null");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Drawable result) {
			ImageView imgView = (ImageView) findViewById(R.id.imageView1);
			imgView.setImageDrawable(result);
		}
		
	}
	
	public void randomPicture(View view) {
		new DownloadPicturesTask().execute(GET_URL);
	}
//		try {
//			HttpClient client = new DefaultHttpClient();
//			String getURL = "http://uwcse403changemymoodv0.herokuapp.com/api/flickr/";
//			HttpGet get = new HttpGet(getURL);
//			HttpResponse responseGet = client.execute(get);
//			HttpEntity resEntityGet = responseGet.getEntity();
//			if (resEntityGet != null) {
//				// do something with the response
//				String rsp = EntityUtils.toString(resEntityGet);
//				Log.i("GET RESPONSE", rsp);
//
//				// THIS IS FUCKING RETARTED
//				rsp = rsp.replaceAll("\\\\", "");
//				rsp = rsp.replaceAll("\"", "\\\"");
//				rsp = rsp.substring(1, rsp.length() - 1);
//
//				if (rsp.startsWith("\"{\"")) {
//					System.out.println("YES");
//				} else {
//					System.out.println("FAIL: " + rsp);
//				}
//
//				// JSONObject json = new
//				// JSONObject("{\"url\": \"http://static.flickr.com/8473/8085137114_722767b405.jpg\"}");
//				JSONObject json = new JSONObject(rsp.toString());
//				String link = json.getString("url");
//				URL url = new URL(link);
//			
//				InputStream is = (InputStream) url.getContent();
//				Drawable image = Drawable.createFromStream(is, "src");
//				ImageView imgView = (ImageView) findViewById(R.id.imageView1);
//				imgView.setImageDrawable(image);
//			} else {
//				Log.d("TEST", "resEntityGet is null");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
//	private class GetHandler extends AsyncTask<String, Integer, URL> {

}
