package cmm.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoFullPage extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webvideo_full);

		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		VideoView videoView = (VideoView) findViewById(R.id.webfullvideo);
		MediaController mc = new MediaController(this); 
		mc.setEnabled(true);
		videoView.setMediaController(mc); 
		videoView.setVideoURI(Uri.parse("rtsp://v1.cache4.c.youtube.com/CiILENy73wIaGQn6nXA54I8tyRMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp")); 
		videoView.requestFocus(); 
		videoView.showContextMenu();
		videoView.start();
	}
}
