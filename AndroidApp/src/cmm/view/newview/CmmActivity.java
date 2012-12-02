package cmm.view.newview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cmm.model.Content;
import cmm.model.ContentStorage;
import cmm.model.Mood;
import cmm.model.Rater;
import cmm.view.R;
import cmm.view.newview.buttonscontrol.ButtonsControlFragment;
import cmm.view.newview.contentdisplay.ContentDisplayFragment;
import cmm.view.newview.navigation.NavigationFragment;

public class CmmActivity extends FragmentActivity {
	/* debug log string */
	private static final String TAG = "CmmActivity";

	/* Screen Size Ratio For Content Display */
	private static final double CONTENT_W_OVER_H = 16.0 / 9.0;

	/* Text Views */
	private TextView[] ui_textviews;
	
	/* Model Objects */
	private ContentStorage contentStorage;
	private Rater rater;

	// Shake
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity
	private boolean hasShaken;
	private boolean initialize;

	/* UI objects */
	private Point ui_dimension;
	private RelativeLayout ui_contentLayout;
	private NavigationFragment navigationFragment;
	private ContentDisplayFragment contentFragment;
	private ButtonsControlFragment buttonsControlFragment;
	private ViewGroup ui_content_bg;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.cmm_main);

		setupComponents();
		handleEvents();
		doLayout();

		rater = new Rater(buttonsControlFragment);
		contentStorage = new ContentStorage(contentFragment, buttonsControlFragment, ui_textviews);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		contentFragment.cleanup();
	}

	@Override
	protected void onPause() {
		super.onPause();
		contentFragment.cleanup(); // TODO: is this needed?
	}

	// http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it
	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	// http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it
	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onStop();
	}

	public Point getDimension() {
		return ui_dimension;
	}
	
	/*
	 * Setup the ui components
	 */
	private void setupComponents() {
		// Find screen size
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		size.x = display.getWidth();
		size.y = display.getHeight();
		Log.d("CmmActivity", "x: " + size.x + " ,y:" + size.y);

		ui_dimension = size;

		// set content layout
		ui_contentLayout = (RelativeLayout) this
				.findViewById(R.id.content_layout);

		// Sensor Event setups
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;

		// for initial picture
		initialize = false;
		ui_content_bg = (ViewGroup) findViewById(R.id.content_layout);
		
		// content info
		ui_textviews = new TextView[]{
				(TextView)findViewById(R.id.ups_number),
				(TextView)findViewById(R.id.downs_number)};
	}

	public void facebook_signintest(View view){
		Intent i = new Intent(this, FacebookHandler.class);
		startActivity(i);
	}
	
	/*
	 * Add listeners if necessary
	 */
	private void handleEvents() {

	}

	/*
	 * Resize layout if necessary
	 */
	private void doLayout() {
		// TODO
		// Hard set layout
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		// Auto adjust content layout
		// Gets the layout params that will allow you to resize the layout
		android.view.ViewGroup.LayoutParams params = (android.view.ViewGroup.LayoutParams) ui_contentLayout
				.getLayoutParams();
		// Changes the height and width to the specified *pixels*
		params.height = (int) (1.0 * ui_dimension.x / CONTENT_W_OVER_H);

		// get an instance of FragmentTransaction from your Activity
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();

		// add navigation fragment
		this.navigationFragment = new NavigationFragment(this);
		fragmentTransaction.add(R.id.navigation_fragment, navigationFragment);

		// add content fragment
		this.contentFragment = ContentDisplayFragment.getInstance(this);
		fragmentTransaction.add(R.id.content_fragment, contentFragment);

		// add buttoncontrol Fragment
		this.buttonsControlFragment = ButtonsControlFragment.getInstance(this);
		fragmentTransaction.add(R.id.buttonscontrol_fragment,
				buttonsControlFragment);

		fragmentTransaction.commit();
	}

	/**
	 * Display Next Image
	 * 
	 * @param mood
	 */
	public void displayNextImage(Mood mood) {
		if (!this.initialize) {
			this.initialize = true;
			ui_content_bg.setBackgroundColor(Color.TRANSPARENT);
		}
		contentFragment.disableButtons();
		contentFragment.showButton();
		contentStorage.getNextImage(mood);
	}

	/**
	 * Display Next Video
	 * 
	 * @param mood
	 */
	public void displayNextVideo(Mood mood) {
		if (!this.initialize) {
			this.initialize = true;
			ui_content_bg.setBackgroundColor(Color.TRANSPARENT);
		}
		contentFragment.disableButtons();
		contentFragment.showButton();
		contentStorage.getNextVideo(mood);
		// new GetVideoTask(this).execute(mood.ordinal(),
		// Content.VIDEO.ordinal());
	}

	/**
	 * Next button event
	 * 
	 * @onClick
	 * @param view
	 */
	public void nextContent(View view) {
		Content curCon = navigationFragment.getContent();
		Mood curMood = navigationFragment.getMood();
		if (curCon != null && curMood != null) {
			// If valid content and mood
			if (curCon == Content.PICTURE) {
				// display pic
				displayNextImage(curMood);
			} else if (curCon == Content.VIDEO) {
				// display video
				displayNextVideo(curMood);
			}
		}
	}

	/**
	 * Prev button event
	 * 
	 * @onClick
	 * @param view
	 */
	public void prevContent(View view) {
		Content curCon = navigationFragment.getContent();
		Mood curMood = navigationFragment.getMood();
		if (curCon != null && curMood != null) {
			// If valid content and mood
			if (curCon == Content.PICTURE) {
				// display pic
				displayPrevImage(curMood);
			} else if (curCon == Content.VIDEO) {
				// display vid
				displayPrevVideo(curMood);
			}
		}
	}

	/**
	 * When thumbs up is clicked
	 * 
	 * @onClick
	 * @param view
	 */
	public void thumbsUp(View view) {
		String mid = contentStorage.getMid();
		if (mid != null) {
			buttonsControlFragment.DisableButton(true);
			contentStorage.ratedMid(mid, true);
			rater.rateThumbsUp(mid);
		}
	}

	/**
	 * When thumbs down is clicked
	 */
	public void thumbsDown(View view) {
		String mid = contentStorage.getMid();
		if (mid != null) {
			buttonsControlFragment.DisableButton(false);
			contentStorage.ratedMid(mid, false);
			rater.rateThumbsDown(mid);
		}
	}

	public void displayRateResponse(boolean isSuccess) {
		String msg = isSuccess ? getResources().getString(
				R.string.rate_success_msg) : getResources().getString(
				R.string.rate_fail_msg);
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Show new content
	 */
	public void newContent() {
		Content curCon = navigationFragment.getContent();
		Mood curMood = navigationFragment.getMood();
		if (curCon != null && curMood != null) {
			if (curCon == Content.PICTURE) {
				displayNewImage(curMood);
			} else if (curCon == Content.VIDEO) {
				displayNewVideo(curMood);
			}
		}
	}

	// Displaying previuos image
	private void displayPrevImage(Mood mood) {
		contentFragment.showButton();
		if (!contentStorage.getPrevImage(mood)) {
			Toast.makeText(getApplicationContext(), R.string.no_prev,
					Toast.LENGTH_SHORT).show();
			contentFragment.hideButtons();
		}
	}

	// Displaying previous video
	private void displayPrevVideo(Mood mood) {
		contentFragment.showButton();
		if (!contentStorage.getPrevVideo(mood)) {
			Toast.makeText(getApplicationContext(), R.string.no_prev,
					Toast.LENGTH_SHORT).show();
			contentFragment.hideButtons();
		}
	}

	// Display new image
	private void displayNewImage(Mood mood) {
		contentFragment.disableButtons();
		contentFragment.showButton();
		contentStorage.getNewImage(mood);
	}

	// Display new video
	private void displayNewVideo(Mood mood) {
		contentFragment.disableButtons();
		contentFragment.showButton();
		contentStorage.getNewVideo(mood);
	}

	/*
	 * From stackoverflow:
	 * http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it
	 */
	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter

			if (mAccel > 2 && !hasShaken) {
				mSensorManager.unregisterListener(mSensorListener);
				hasShaken = true;
				newContent();
				startTimerThread();
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	private void startTimerThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				restartShake();
			}
		}).start();
	}

	private void restartShake() {
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		hasShaken = false;
	}
}
