package cmm.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import cmm.view.newview.CmmActivity;

public class SplashActivity extends Activity {
	protected int _loading = 500;
	protected int _loadfinal = 1000;

	private Thread splashThread;

	private ImageView ui_imageview;
	private List<Integer> loadscreens;
	private Map<Integer, Animation> animes;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash_layout);

		final SplashActivity splashScreen = this;
		ui_imageview = (ImageView) findViewById(R.id.splash_img);
		
		loadscreens = new ArrayList<Integer>(4);
		loadscreens.add(R.drawable.load1);
		loadscreens.add(R.drawable.load2);
		loadscreens.add(R.drawable.load3);
		loadscreens.add(R.drawable.loadfinal);
		
		animes = new HashMap<Integer, Animation>();
		for (Integer did : loadscreens) {
			Animation ani = AnimationUtils.loadAnimation(this, R.anim.splashin);
			ani.setAnimationListener(new MyAnimationListener(did));
			animes.put(did, ani);
			Log.d("Splash", "did: " + did + " in map");
		}
		
		Log.d("Splash", "Getting " + loadscreens.get(0));
		this.loadImg(loadscreens.get(0));

		splashThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						for (int i = 1; i < loadscreens.size(); i++) {
							wait(_loading);
							runOnUiThread(new MyRunnable(loadscreens.get(i)));
						}
						wait(_loadfinal);
					}
				} catch (InterruptedException e) {
				} finally {
					finish();

					Intent intent = new Intent();
					intent.setClass(splashScreen, CmmActivity.class);
					startActivity(intent);
				}
			}
		};

		splashThread.start();
	}

	@Override
	public void onBackPressed() {

	}
	
	private void loadImg(int did) {
		ui_imageview.setBackgroundResource(did);
		Animation ani = animes.get(did);
		ui_imageview.startAnimation(ani);
	}
	
	private class MyRunnable implements Runnable {
		private int did;
		
		public MyRunnable(int did) {
			this.did = did;
		}
		
		@Override
		public void run() {
			loadImg(did);
		}
		
	}
	
	private class MyAnimationListener implements AnimationListener{
		private int did;
		
		public MyAnimationListener(int idx) {
			this.did = idx;
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			ui_imageview.setBackgroundResource(did);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {}

		@Override
		public void onAnimationStart(Animation animation) {}
		
	}
}
