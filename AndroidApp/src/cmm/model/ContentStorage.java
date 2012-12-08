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

import com.facebook.FacebookActivity;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import cmm.view.R;
import cmm.view.newview.CmmActivity;
import cmm.view.newview.CmmFacebookActivity;
import cmm.view.newview.buttonscontrol.ButtonsControlFragment;
import cmm.view.newview.contentdisplay.ContentDisplayFragment;
import cmm.view.newview.contentinfo.ContentInfoFragment;

public class ContentStorage {
	private static final String TAG = "ContentStorage";
	private static final String INIT = "contentstorageinit";
	private static final String SHARD_TAG = "ContentStorageShardPref";

	private Map<Mood, List<String>> imageMap;
	private Map<Mood, List<String>> videoMap;

	private Map<Mood, Integer> imageIndex;
	private Map<Mood, Integer> videoIndex;

	private Map<String, ContentInfo> midToImage;
	private Map<String, ContentInfo> midToVideo;

	private String cur_mid;

	private Map<String, Integer> canRateMap;
	private Map<String, Boolean> realRate;

	private ContentDisplayFragment contentFragment;
	private ButtonsControlFragment buttonsControlFragment;
	private ContentInfoFragment contentInfoFragment;

	/* Set Content info */
	// private TextView[] ui_contentinfo;
	private String up_info;
	private String down_info;

	// private boolean init;
	private CmmActivity activity;

	private static ContentStorage instance;

	public static ContentStorage getInstance(
			ContentDisplayFragment contentFragment,
			ButtonsControlFragment buttonControlFragment,
			ContentInfoFragment contentInfoFragment, CmmActivity activity) {

		Log.d(TAG, "Get Instance");
		if (instance == null) {
			instance = new ContentStorage(contentFragment,
					buttonControlFragment, contentInfoFragment, activity);
		}
		instance.initImageMap();
		instance.initVideoMap();
		return instance;
	}

	private ContentStorage(ContentDisplayFragment contentFragment,
			ButtonsControlFragment buttonControlFragment,
			ContentInfoFragment contentInfoFragment, CmmActivity activity) { // TextView[]
																				// list)
																				// {
		this.contentFragment = contentFragment;
		this.buttonsControlFragment = buttonControlFragment;

		imageMap = new HashMap<Mood, List<String>>();
		videoMap = new HashMap<Mood, List<String>>();

		imageIndex = new HashMap<Mood, Integer>();
		videoIndex = new HashMap<Mood, Integer>();

		midToImage = new HashMap<String, ContentInfo>();
		midToVideo = new HashMap<String, ContentInfo>();

		canRateMap = new HashMap<String, Integer>();
		realRate = new HashMap<String, Boolean>();

		// ui_contentinfo = list;
		this.contentInfoFragment = contentInfoFragment;

		for (Mood m : Mood.values()) {
			imageMap.put(m, new ArrayList<String>());
			videoMap.put(m, new ArrayList<String>());
			imageIndex.put(m, -1);
			videoIndex.put(m, -1);
		}

		this.activity = activity;

		Log.d(TAG, "Finish Constructor");
	}

	private void initImageMap() {
		imageMap.clear();
		imageMap = new HashMap<Mood, List<String>>();
		for (Mood m : Mood.values()) {
			imageMap.put(m, new ArrayList<String>());
		}
		Log.d(TAG, "Cleared image map");
	}

	private void initVideoMap() {
		videoMap.clear();
		videoMap = new HashMap<Mood, List<String>>();
		for (Mood m : Mood.values()) {
			videoMap.put(m, new ArrayList<String>());
		}
		Log.d(TAG, "Cleared video map");
	}

	public String getMid() {
		return this.cur_mid;
	}

	public String getCurrentVideoUrl() {
		if (!midToVideo.containsKey(cur_mid)) {
			throw new IllegalArgumentException("currently not video");
		}

		return midToVideo.get(cur_mid).getVideo();
	}

	private boolean canRate(String mid) {
		if (canRateMap.containsKey(mid)) {
			if (CmmFacebookActivity.isSignedIn) {
				return canRateMap.get(mid) == -1;
			} else {
				return true;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	private boolean getRatedValue(String mid) {
		if (canRateMap.containsKey(mid) && canRateMap.get(mid) != -1) {
			return canRateMap.get(mid) == Rate.THUMBSUP.ordinal();
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void ratedMid(String mid, boolean isThumbsUp, boolean isRealRate) {
		if (mid == null) {
			throw new IllegalArgumentException("null mid");
		}

		if (midToImage.containsKey(mid)) {
			ContentInfo contentInfo = midToImage.get(mid);
			String up = contentInfo.getUpInfo();
			String down = contentInfo.getDownInfo();

			if (isRealRate) {
				if (isThumbsUp) {
					int up_int = Integer.parseInt(up);
					up = "" + (up_int + 1);
				} else {
					int down_int = Integer.parseInt(down);
					down = "" + (down_int + 1);
				}
			}

			ContentInfo newInfo = new ContentInfo(contentInfo.getVideo(),
					contentInfo.getPicture(), up, down);
			midToImage.put(mid, newInfo);

			up_info = midToImage.get(mid).getUpInfo();
			down_info = midToImage.get(mid).getDownInfo();
		} else if (midToVideo.containsKey(mid)) {
			ContentInfo contentInfo = midToVideo.get(mid);

			String up = contentInfo.getUpInfo();
			String down = contentInfo.getDownInfo();

			if (isRealRate) {
				if (isThumbsUp) {
					int up_int = Integer.parseInt(up);
					up = "" + (up_int + 1);
				} else {
					int down_int = Integer.parseInt(down);
					down = "" + (down_int + 1);
				}
			}

			ContentInfo newInfo = new ContentInfo(contentInfo.getVideo(),
					contentInfo.getPicture(), up, down);

			//
			// ContentInfo newInfo = new ContentInfo(contentInfo.getVideo(),
			// contentInfo.getPicture(), contentInfo.getUpInfo(),
			// contentInfo.getDownInfo());
			midToVideo.put(mid, newInfo);

			up_info = midToVideo.get(mid).getUpInfo();
			down_info = midToVideo.get(mid).getDownInfo();
		}

		if (isRealRate) {
			canRateMap.put(mid, isThumbsUp ? Rate.THUMBSUP.ordinal()
					: Rate.THUMBSDOWN.ordinal());
		}
		// this.setText();
	}

	/**
	 * Display Next Image
	 * 
	 * @param mood
	 */
	public void getNextImage(Mood mood) {
		Log.d(TAG, "getNewImage");
		if (mood == null) {
			throw new IllegalArgumentException("Null mood");
		}
		// initializeMaps();

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
			Drawable image = midToImage.get(mid).getPicture();
			up_info = midToImage.get(mid).getUpInfo();
			down_info = midToImage.get(mid).getDownInfo();
			imageIndex.put(mood, imgIndex);
			contentFragment.displayImage(image);

			this.cur_mid = mid;
			setText();

			contentFragment.EnableButtons();
			if (canRate(mid)) {
				buttonsControlFragment.EnableButton();
			} else {
				buttonsControlFragment
						.DisableButton(getRatedValue(this.cur_mid));
			}
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
			Log.d(TAG, "img index " + imgIndex + ", " + mood.value + " is < 0");
			contentFragment.EnableButtons();
			return false;
		} else {
			Log.d(TAG, "getting prev image");
			String mid = imageMidList.get(imgIndex);
			Drawable image = midToImage.get(mid).getPicture();
			up_info = midToImage.get(mid).getUpInfo();
			down_info = midToImage.get(mid).getDownInfo();
			imageIndex.put(mood, imgIndex);
			contentFragment.displayImage(image);

			this.cur_mid = mid;

			if (canRate(mid)) {
				buttonsControlFragment.EnableButton();
			} else {
				buttonsControlFragment
						.DisableButton(getRatedValue(this.cur_mid));
			}

			setText();
		}

		contentFragment.EnableButtons();
		return true;
	}

	public void getNewImage(Mood mood) {
		if (mood == null) {
			throw new IllegalArgumentException("Null mood");
		}
		// initializeMaps();
		new GetPictureTask(this).execute(mood.ordinal(),
				Content.PICTURE.ordinal());
	}

	public void getNextVideo(Mood mood) {
		if (mood == null) {
			throw new IllegalArgumentException("Null mood");
		}
		// initializeMaps();

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
			String videoUrl = midToVideo.get(mid).getVideo();
			up_info = midToVideo.get(mid).getUpInfo();
			down_info = midToVideo.get(mid).getDownInfo();
			videoIndex.put(mood, vidIndex);
			contentFragment.displayVideo(videoUrl);
			contentFragment.EnableButtons();

			this.cur_mid = mid;

			if (canRate(mid)) {
				buttonsControlFragment.EnableButton();
			} else {
				buttonsControlFragment
						.DisableButton(getRatedValue(this.cur_mid));
			}

			setText();
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
			String videoUrl = midToVideo.get(mid).getVideo();
			up_info = midToVideo.get(mid).getUpInfo();
			down_info = midToVideo.get(mid).getDownInfo();
			videoIndex.put(mood, vidIndex);
			contentFragment.displayVideo(videoUrl);

			this.cur_mid = mid;

			if (canRate(mid)) {
				buttonsControlFragment.EnableButton();
			} else {
				buttonsControlFragment
						.DisableButton(getRatedValue(this.cur_mid));
			}

			setText();

		}
		contentFragment.EnableButtons();
		return true;
	}

	public void getNewVideo(Mood mood) {
		if (mood == null) {
			throw new IllegalArgumentException("Null mood");
		}
		// initializeMaps();

		new GetVideoTask(this).execute(mood.ordinal(), Content.VIDEO.ordinal());
	}

	public void fullScreen() {
		if (cur_mid == null) {
			Log.d(TAG, "null mid");
			return; // throw new IllegalStateException();
		}

		if (midToImage.containsKey(cur_mid)) {
			contentFragment.displayFullImage(midToImage.get(cur_mid)
					.getPicture());
		} else if (midToVideo.containsKey(cur_mid)) {
			contentFragment
					.displayFullVideo(midToVideo.get(cur_mid).getVideo());
		} else {
			throw new IllegalArgumentException("cur_mid corrupted");
		}
	}

	public void resumeFromFullScreen() {
		if (cur_mid == null) {
			Log.d(TAG, "no mid");
			return;
		}

		// For Content
		if (midToImage.containsKey(cur_mid)) {
			Log.d(TAG, "redisplay Picture");
			contentFragment.displayImage(midToImage.get(cur_mid).getPicture(),
					true);
		} else if (midToVideo.containsKey(cur_mid)) {
			contentFragment.displayVideo(midToVideo.get(cur_mid).getVideo(),
					true);
		} else {
			throw new IllegalArgumentException("cur_mid corrupted");
		}
		
		// For rate buttons
		if (canRate(cur_mid)) {
			buttonsControlFragment.EnableButton();
		} else {
			buttonsControlFragment
					.DisableButton(getRatedValue(this.cur_mid));
		}
		
		contentFragment.EnableButtons();
	}

	public void setText() {
		// ui_contentinfo[0].setText("Up: " + up_info);
		// ui_contentinfo[1].setText("Down: " + down_info);
		this.contentInfoFragment.setText(up_info, down_info);
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

					if (!canRateMap.containsKey(cs.cur_mid)) {
						canRateMap.put(cs.cur_mid, -1);
					}

					this.cs.up_info = json.getString("ups");
					this.cs.down_info = json.getString("downs");

					// insert image
					List<String> imageMidList = imageMap.get(mood);
					int imgIndex = imageIndex.get(mood);
					imageMidList.add(json.getString("mid"));
					midToImage.put(json.getString("mid"), new ContentInfo(null,
							image, this.cs.up_info, this.cs.down_info));
					Log.d(TAG, imageMidList.toString());
					imgIndex = imageMidList.size() - 1;
					imageIndex.put(mood, imgIndex);
					Log.d(TAG, "Inserted Image (" + mood.value + ", "
							+ content.value + ")to list, size = " + imgIndex);

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
			if (result == null) {
				cs.activity.failedToast();
				Log.d(TAG, "No Wireless");
				return;
			}
			contentFragment.displayImage(result);
			contentFragment.EnableButtons();
			if (canRate(cs.cur_mid)) {
				buttonsControlFragment.EnableButton();
			} else {
				buttonsControlFragment.DisableButton(getRatedValue(cs.cur_mid));
			}
			// Enable Rating here
			setText();
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
					this.cs.up_info = jresult.getString("ups");
					this.cs.down_info = jresult.getString("downs");

					// insert video
					List<String> videoMidList = videoMap.get(mood);
					int vidIndex = videoIndex.get(mood);
					videoMidList.add(cs.cur_mid);
					midToVideo.put(cs.cur_mid,
							new ContentInfo(jresult.getString("url"), null,
									this.cs.up_info, this.cs.down_info));
					vidIndex = videoMidList.size() - 1;
					videoIndex.put(mood, vidIndex);
					Log.d(TAG, "Inserted video to list, idx = " + vidIndex
							+ ", totsize = " + videoMidList.size());

					// mid logic
					if (!canRateMap.containsKey(cs.cur_mid)) {
						canRateMap.put(cs.cur_mid, -1);
					}

					return jresult.getString("url");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String str) {
			if (str == null) {
				cs.activity.failedToast();
				Log.d(TAG, "No Wireless");
				return;
			}

			contentFragment.displayVideo(str);
			contentFragment.EnableButtons();

			if (canRate(cs.cur_mid)) {
				buttonsControlFragment.EnableButton();
			} else {
				buttonsControlFragment.DisableButton(getRatedValue(cs.cur_mid));
			}
			setText();
		}
	}
}
