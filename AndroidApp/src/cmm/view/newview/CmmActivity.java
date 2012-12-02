package cmm.view.newview;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.Toast;
import cmm.model.Content;
import cmm.model.ContentStorage;
import cmm.model.Mood;
import cmm.view.R;
import cmm.view.newview.contentdisplay.ContentDisplayFragment;
import cmm.view.newview.navigation.NavigationFragment;

public class CmmActivity extends FragmentActivity {
	/* debug log string */
	private static final String TAG = "CmmActivity";

	private static final double CONTENT_W_OVER_H = 16.0 / 9.0;

	/* Model Objects */
	private ContentStorage contentStorage;
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity
	private boolean hasShaken;
	private boolean initialize;

	/* UI objects */
	private Point ui_dimension;
	private LinearLayout ui_contentLayout;
	private NavigationFragment navigationFragment;
	private ContentDisplayFragment contentFragment;
	private ViewGroup ui_content_bg;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.cmm_main);

		setupComponents();
		handleEvents();
		doLayout();

		contentStorage = new ContentStorage(contentFragment);
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
		ui_contentLayout = (LinearLayout) this
				.findViewById(R.id.content_layout);

		// Sensor Event setups
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;

		// for picture
		initialize = false;
		ui_content_bg = (ViewGroup) findViewById(R.id.content_layout);
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

		fragmentTransaction.commit();
	}

	public void displayNextImage(Mood mood) {
		if (!this.initialize) {
			this.initialize = true;
			ui_content_bg.setBackgroundColor(Color.TRANSPARENT);
		}
		contentFragment.disableButtons();
		contentFragment.showButton();
		contentStorage.getNextImage(mood);
		// new GetPictureTask(this).execute(mood.ordinal(),
		// Content.PICTURE.ordinal());
	}

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

	public void nextContent(View view) {
		Content curCon = navigationFragment.getContent();
		Mood curMood = navigationFragment.getMood();
		if (curCon != null && curMood != null) {
			if (curCon == Content.PICTURE) {
				displayNextImage(curMood);
			} else if (curCon == Content.VIDEO) {
				displayNextVideo(curMood);
			}
		}
	}

	public void prevContent(View view) {
		Content curCon = navigationFragment.getContent();
		Mood curMood = navigationFragment.getMood();
		if (curCon != null && curMood != null) {
			if (curCon == Content.PICTURE) {
				displayPrevImage(curMood);
			} else if (curCon == Content.VIDEO) {
				displayPrevVideo(curMood);
			}
		}
	}

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

	private void displayPrevImage(Mood mood) {
		contentFragment.showButton();
		if (!contentStorage.getPrevImage(mood)) {
			Toast.makeText(getApplicationContext(), R.string.no_prev,
					Toast.LENGTH_SHORT).show();
			contentFragment.hideButtons();
		}
	}

	private void displayPrevVideo(Mood mood) {
		contentFragment.showButton();
		if (!contentStorage.getPrevVideo(mood)) {
			Toast.makeText(getApplicationContext(), R.string.no_prev,
					Toast.LENGTH_SHORT).show();
			contentFragment.hideButtons();
		}
	}

	private void displayNewImage(Mood mood) {
		contentFragment.disableButtons();
		contentFragment.showButton();
		contentStorage.getNewImage(mood);
	}

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
