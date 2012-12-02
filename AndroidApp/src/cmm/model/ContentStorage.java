package cmm.model;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import cmm.view.newview.CmmActivity;
import cmm.view.newview.contentdisplay.ContentDisplayFragment;

public class ContentStorage {
	private static final String TAG = "ContentStorage";

	private Map<Mood, List<String>> imageMap;
	private Map<Mood, List<String>> videoMap;

	private Map<Mood, Integer> imageIndex;
	private Map<Mood, Integer> videoIndex;

	private Map<String, Drawable> midToImage;
	private Map<String, String> midToVideo;

	private String cur_mid;

	private ContentDisplayFragment contentFragment;

	public ContentStorage(ContentDisplayFragment contentFragment) {
		this.contentFragment = contentFragment;

		imageMap = new HashMap<Mood, List<String>>();
		videoMap = new HashMap<Mood, List<String>>();

		imageIndex = new HashMap<Mood, Integer>();
		videoIndex = new HashMap<Mood, Integer>();

		midToImage = new HashMap<String, Drawable>();
		midToVideo = new HashMap<String, String>();

		for (Mood m : Mood.values()) {
			imageMap.put(m, new ArrayList<String>());
			videoMap.put(m, new ArrayList<String>());
			imageIndex.put(m, 0);
			videoIndex.put(m, 0);
		}
	}

	public String getMid() {
		return this.cur_mid;
	}
	
	public void getNextImage(Mood mood) {
		if (mood == null) {
			throw new IllegalArgumentException("Null mood");
		}

		List<String> imageMidList = imageMap.get(mood);
		int imgIndex = imageIndex.get(mood);
		imgIndex++;

		if (imgIndex >= imageMidList.size()) {
			// make new thread
			new GetPictureTask(this).execute(mood.ordinal(),
					Content.PICTURE.ordinal());
		} else {
			// show image in list
			String mid = imageMidList.get(imgIndex);
			Drawable image = midToImage.get(mid);
			imageIndex.put(mood, imgIndex);
			contentFragment.displayImage(image);

			this.cur_mid = mid;

			contentFragment.EnableButtons();
		}
	}

	public boolean getPrevImage(Mood mood) {
		if (mood == null) {
			throw new IllegalArgumentException("Null mood");
		}

		List<String> imageMidList = imageMap.get(mood);
		int imgIndex = imageIndex.get(mood);
		imgIndex--;

		if (imgIndex < 0) {
			// Maybe display something here?
			Log.d(TAG, "img index " + mood.value + " is < 0");
			contentFragment.EnableButtons();
			return false;
		} else {
			Log.d(TAG, "getting prev image");
			String mid = imageMidList.get(imgIndex);
			Drawable image = midToImage.get(mid);
			imageIndex.put(mood, imgIndex);
			contentFragment.displayImage(image);

			this.cur_mid = mid;
		}

		contentFragment.EnableButtons();
		return true;
	}

	public void getNewImage(Mood mood) {
		if (mood == null) {
			throw new IllegalArgumentException("Null mood");
		}

		new GetPictureTask(this).execute(mood.ordinal(),
				Content.PICTURE.ordinal());
	}

	public void getNextVideo(Mood mood) {
		if (mood == null) {
			throw new IllegalArgumentException("Null mood");
		}

		List<String> videoMidList = videoMap.get(mood);
		int vidIndex = videoIndex.get(mood);
		vidIndex++;

		if (vidIndex >= videoMidList.size()) {
			// make new thread
			new GetVideoTask(this).execute(mood.ordinal(),
					Content.VIDEO.ordinal());
		} else {
			// show video in list
			String mid = videoMidList.get(vidIndex);
			String videoUrl = midToVideo.get(mid);
			videoIndex.put(mood, vidIndex);
			contentFragment.displayVideo(videoUrl);
			contentFragment.EnableButtons();
			
			this.cur_mid = mid;
		}

	}

	public boolean getPrevVideo(Mood mood) {
		if (mood == null) {
			throw new IllegalArgumentException("Null mood");
		}

		List<String> videoMidList = videoMap.get(mood);
		int vidIndex = videoIndex.get(mood);
		vidIndex--;

		if (vidIndex < 0) {
			// Maybe display something here?
			Log.d(TAG, "vid index " + mood.value + " is < 0");
			contentFragment.EnableButtons();
			return false;
		} else {
			Log.d(TAG, "getting prev video idx: " + vidIndex + ", totsize = "
					+ videoMidList.size());
			String mid = videoMidList.get(vidIndex);
			String videoUrl = midToVideo.get(mid);
			videoIndex.put(mood, vidIndex);
			contentFragment.displayVideo(videoUrl);
			
			this.cur_mid = mid;
		}
		contentFragment.EnableButtons();
		return true;
	}

	public void getNewVideo(Mood mood) {
		if (mood == null) {
			throw new IllegalArgumentException("Null mood");
		}

		new GetVideoTask(this).execute(mood.ordinal(), Content.VIDEO.ordinal());
	}

	/**
	 * Android 4.X Need to run networking code on another thread
	 * 
	 * @author hunlan
	 * 
	 */
	private class GetPictureTask extends AsyncTask<Integer, Integer, Drawable> {
		private ContentStorage cs;

		public GetPictureTask(ContentStorage cs) {
			super();
			this.cs = cs;
		}

		// 0th = mood, 1st = content
		@Override
		protected Drawable doInBackground(Integer... params) {
			try {
				Mood mood = Mood.fromInt(params[0]);
				Content content = Content.fromInt(params[1]);

				HttpClient client = new DefaultHttpClient();
				String url_str = UrlProvider.getPictureUrl(mood, content);

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
					this.cs.cur_mid = json.getString("mid");
					
					// insert image
					List<String> imageMidList = imageMap.get(mood);
					int imgIndex = imageIndex.get(mood);
					imageMidList.add(json.getString("mid"));
					midToImage.put(json.getString("mid"), image);
					imgIndex = imageMidList.size() - 1;
					imageIndex.put(mood, imgIndex);
					Log.d(TAG, "Inserted Image to list");

					

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
			contentFragment.EnableButtons();
			// Enable Rating here
		}
	}

	/**
	 * Video Task
	 * 
	 * @author hunlan
	 * 
	 */
	private class GetVideoTask extends AsyncTask<Integer, Integer, String> {
		private ContentStorage cs;
		private String yid;

		public GetVideoTask(ContentStorage cs) {
			super();
			this.cs = cs;
		}

		@Override
		protected String doInBackground(Integer... arg) {
			try {
				Mood mood = Mood.fromInt(arg[0]);
				Content content = Content.fromInt(arg[1]);

				HttpClient client = new DefaultHttpClient();
				String vd_url = UrlProvider.getVideoUrl(mood, content);

				HttpGet hg = new HttpGet(vd_url);
				HttpResponse hr = client.execute(hg);
				HttpEntity he = hr.getEntity();

				if (he != null) {
					String result = EntityUtils.toString(he);

					JSONObject jresult = new JSONObject(result);
					this.cs.cur_mid = jresult.getString("mid");

					// insert video
					List<String> videoMidList = videoMap.get(mood);
					int vidIndex = videoIndex.get(mood);
					videoMidList.add(cs.cur_mid);
					midToVideo.put(cs.cur_mid, jresult.getString("url"));
					vidIndex = videoMidList.size() - 1;
					videoIndex.put(mood, vidIndex);
					Log.d(TAG, "Inserted video to list, idx = " + vidIndex
							+ ", totsize = " + videoMidList.size());

					return jresult.getString("url");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String str) {
			contentFragment.displayVideo(str);
			contentFragment.EnableButtons();
		}
	}
}
