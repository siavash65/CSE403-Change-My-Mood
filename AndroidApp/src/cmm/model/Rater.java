package cmm.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import cmm.view.newview.buttonscontrol.ButtonsControlFragment;

public class Rater {
	private static final String TAG = "Rater";
	
	private ButtonsControlFragment bsf;

	public Rater(ButtonsControlFragment buttoncontrol_fragment) {
		this.bsf = buttoncontrol_fragment;
	}

	public void rateThumbsUp(String mid) {
		new RatePictureTask().execute(mid, Rate.THUMBSUP.ordinal() + "");
	}
	
	public void rateThumbsDown(String mid) {
		new RatePictureTask().execute(mid, Rate.THUMBSDOWN.ordinal() + "");
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
			bsf.displayResponse(s);
		}

	}
}
