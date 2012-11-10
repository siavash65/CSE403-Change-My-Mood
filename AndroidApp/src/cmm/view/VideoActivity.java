package cmm.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;
import cmm.model.FacebookHandler;

public class VideoActivity extends Activity{
	private MenuInflater inflater;
	private Menu menu;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_layout);

		VideoView videoView = (VideoView) findViewById(R.id.video_view);
        MediaController mc = new MediaController(this);
        mc.setEnabled(true);
        videoView.setMediaController(mc);
        videoView.setVideoURI(Uri.parse("rtsp://v1.cache4.c.youtube.com/CiILENy73wIaGQn6nXA54I8tyRMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp"));
        videoView.requestFocus();
        videoView.showContextMenu();
        videoView.start();
	}
	
	// create menu
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	inflater = getMenuInflater();
    	this.menu = menu;
    	
    	if(FacebookHandler.getInstance().getStatus()){
    		inflater.inflate(R.menu.menu_login, menu);
    	}else{
    		inflater.inflate(R.menu.menu_basic, menu);
    	}
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
    			FacebookHandler.getInstance().doSignout(this, menu, inflater);
				return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
    	}
    }
}
