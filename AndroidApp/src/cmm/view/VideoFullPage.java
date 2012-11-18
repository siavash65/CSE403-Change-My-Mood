package cmm.view;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoFullPage extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webvideo_full);

		Bundle bundle = getIntent().getExtras();
		
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}

		String url = bundle.getString("URL");
		new GetYoutubeRTSP(this).execute(url);
	}
	
	// get rtsp address for videoview from youtube
	private class GetYoutubeRTSP extends AsyncTask<String, String, String>{
		private VideoFullPage vp;

		public GetYoutubeRTSP(VideoFullPage vfp){
			super();
			vp = vfp;
		}
		
		@Override
		protected String doInBackground(String... params) {
			try{
				String vid = params[0].split("=", 2)[1];
				//url to get rtsp url
				String url = "http://gdata.youtube.com/feeds/mobile/videos/" + vid;
				
				//request and parsing xml
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				URL rtsp_url = new URL(url);
				HttpURLConnection con = (HttpURLConnection)rtsp_url.openConnection();
				Document doc = db.parse(con.getInputStream());
				Element e = doc.getDocumentElement();
				NodeList list = e.getElementsByTagName("media:content");
				String cursor = params[0];
				for(int i = 0; i < list.getLength(); i++){
					Node node = list.item(i);
					if(node != null){
						NamedNodeMap nm = node.getAttributes();
						HashMap<String, String> map = new HashMap<String, String>();
						for(int j = 0; j < nm.getLength(); j++){
							Attr a = (Attr) nm.item(j);
							map.put(a.getName(), a.getValue());
						}
						
						if(map.containsKey("yt:format")){
							String f = map.get("yt:format");
	                        if (map.containsKey("url"))
	                        {
	                            cursor = map.get("url");
	                        }
	                        if (f.equals("1")){
	                            return cursor;
	                        }
						}
					}
				}
				return cursor;
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		
		// get result from xml and play video
		@Override
		protected void onPostExecute(String result){
			// when rtsp is not available
			if(result == null){
				result = "http://www.youtube.com/watch?v=MhV45iit7m8";
			}
			
			VideoView videoView = (VideoView) findViewById(R.id.webfullvideo);
			MediaController mc = new MediaController(vp); 
			mc.setEnabled(true);
			videoView.setMediaController(mc); 
			videoView.setVideoURI(Uri.parse(result)); 
			videoView.requestFocus(); 
			videoView.showContextMenu();
			videoView.start();
		}
	}
}
