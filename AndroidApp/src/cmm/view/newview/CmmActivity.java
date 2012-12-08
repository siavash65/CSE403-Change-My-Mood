package cmm.view.newview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import cmm.model.Content;
import cmm.model.ContentStorage;
import cmm.model.FacebookHandler;
import cmm.model.Mood;
import cmm.model.Rater;
import cmm.view.AboutUs;
import cmm.view.ContactUs;
import cmm.view.R;
import cmm.view.newview.buttonscontrol.ButtonsControlFragment;
import cmm.view.newview.contentdisplay.ContentDisplayFragment;
import cmm.view.newview.contentinfo.ContentInfoFragment;
import cmm.view.newview.navigation.NavigationFragment;

public class CmmActivity extends FragmentActivity {
	/* debug log string */
	private static final String TAG = "CmmActivity";

	/* Screen Size Ratio For Content Display */
	private static final double CONTENT_W_OVER_H = 16.0 / 9.0;

	/* Text Views */
	// private TextView[] ui_textviews;

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
	private boolean redirected;

	/* UI objects */
	private Point ui_dimension;
	private RelativeLayout ui_contentLayout;
	private NavigationFragment navigationFragment;
	private ContentDisplayFragment contentFragment;
	private ButtonsControlFragment buttonsControlFragment;
	private ContentInfoFragment contentInfoFragment;
	private ViewGroup ui_content_bg;
	private SeekBar ui_progress;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		this.setContentView(R.layout.cmm_main);

		if (!isOnline()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Add the buttons
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User clicked OK button
							startActivity(new Intent(
									Settings.ACTION_WIFI_SETTINGS));
							finish();
						}
					});
			builder.setNegativeButton("Exit",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
							finish();
						}
					});

			// Create the AlertDialog
			AlertDialog dialog = builder.create();
			dialog.show();
			// startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			// this.finish();
		}

		setupComponents();
		handleEvents();
		doLayout();

		rater = new Rater(buttonsControlFragment, this.ui_progress);
		contentStorage = ContentStorage.getInstance(contentFragment,
				buttonsControlFragment, contentInfoFragment, this);
		// get an instance of FragmentTransaction from your Activity
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		// add navigation fragment
		this.navigationFragment = new NavigationFragment(contentStorage);
		fragmentTransaction.add(R.id.navigation_fragment, navigationFragment);
		fragmentTransaction.commit();
	}

	// create menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_basic, menu);
		return true;
	}

	// handle menu activity
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case R.id.aboutus_menu:
			intent.setClass(this, AboutUs.class);
			startActivity(intent);
			return true;
		case R.id.contactus_menu:
			intent.setClass(this, ContactUs.class);
			startActivity(intent);
			return true;
		case R.id.flash:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources()
					.getString(R.string.flash_page)));
			startActivity(intent);
		case R.id.signout_menu:
			// FacebookHandler.getInstance().doSignout(this, menu, inflater);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// reset layout
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		contentFragment.cleanup();
	}

	@Override
	protected void onPause() {
		super.onPause();
		contentFragment.cleanup(); // TODO: is this needed?
		redirected = true;
	}

	// http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it
	@Override
	protected void onResume() {
		super.onResume();
		if (contentStorage != null && redirected) {
			contentStorage.resumeFromFullScreen();
			redirected = false;
		}
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	// http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it
	@Override
	protected void onStop() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onStop();
		redirected = true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		contentStorage.resumeFromFullScreen();
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
		// ui_textviews = new TextView[] {
		// (TextView) super.findViewById(R.id.ups_number),
		// (TextView) super.findViewById(R.id.downs_number) };

		// progress bar
		ui_progress = (SeekBar) findViewById(R.id.progress_bar);
	}

	public void facebook_signintest(View view) {
		Intent i = new Intent(this, CmmFacebookActivity.class);// FacebookHandler.class);
		startActivity(i);
	}

	public void facebook_signin(View view) {
		redirected = true;
		Intent intent = new Intent(this, CmmFacebookActivity.class);
		startActivity(intent);
		// FacebookHandler.getInstance().doSignin(this, getBaseContext());
	}

	public void facebook_signout(View view) {
		// FacebookHandler.getInstance().doSignout(this);
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

		// add content fragment
		this.contentFragment = ContentDisplayFragment.getInstance(this);
		fragmentTransaction.add(R.id.content_fragment, contentFragment);

		// add buttoncontrol Fragment
		this.buttonsControlFragment = ButtonsControlFragment.getInstance(this);
		fragmentTransaction.add(R.id.buttonscontrol_fragment,
				buttonsControlFragment);

		// content info fragment
		this.contentInfoFragment = ContentInfoFragment.getInstance();
		fragmentTransaction
				.add(R.id.content_info_fragment, contentInfoFragment);

		fragmentTransaction.commit();

		// disable progress bar
		this.ui_progress.setEnabled(false);

	}

	/**
	 * Display Next Image
	 * 
	 * @param mood
	 */
	public void displayNextImage(Mood mood) {
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
			rater.updateProgress(true);
			if (CmmFacebookActivity.isSignedIn) {
				rater.rateThumbsUp(mid);
			}
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
			rater.updateProgress(false);
			if (CmmFacebookActivity.isSignedIn) {
				rater.rateThumbsUp(mid);
			}
		}
	}

	/**
	 * Full screen
	 * 
	 * @onClick
	 * @param view
	 */
	public void fullScreen(View view) {
		contentFragment.disableButtons();
		contentFragment.showFullButton();
		contentStorage.fullScreen();
	}

	/**
	 * Go to about us
	 * 
	 * @onClick
	 * @param view
	 */

	public void startAboutUs(View view) {
		Intent intent = new Intent();
		intent.setClass(this, AboutUs.class);
		startActivity(intent);
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

	public void setText(String up_info, String down_info) {
		Log.d(TAG, "Setting up: " + up_info + ", down: " + down_info);
		// ui_textviews[0].setText("Up: " + up_info);
		// ui_textviews[1].setText("Down: " + down_info);
		contentInfoFragment.setText(up_info, down_info);
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

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
