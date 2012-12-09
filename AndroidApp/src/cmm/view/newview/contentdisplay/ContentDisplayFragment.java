package cmm.view.newview.contentdisplay;

import java.io.ByteArrayOutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import cmm.model.Content;
import cmm.view.R;
import cmm.view.VideoFullPage;
import cmm.view.newview.CmmActivity;
import cmm.view.newview.PictureFullActivity;

/**
 * This class is the class for displaying either picture of video
 * 
 * @author hunlan
 * 
 */
public class ContentDisplayFragment extends Fragment {
	/* Debug logger tag */
	private static final String TAG = "ContentDisplayFragment";

	/* Singleton */
	private static ContentDisplayFragment instance;

	/* For updating main */
	private CmmActivity activity;

	/* UI views/components */
	private View ui_view;
	private Content cur_content;
	private PictureDisplayFragment picture_fragment;
	private VideoDisplayFragment video_fragment;

	private Button ui_nextButton;
	private Button ui_prevButton;
	private Button ui_fullButton;

	private Animation anim_fadeoutButton;

	/**
	 * Singleton constructor, pass me activity and i will call the update main
	 * 
	 * @param activity
	 * @return
	 */
	public static ContentDisplayFragment getInstance(CmmActivity activity) {
		if (instance == null) {
			instance = new ContentDisplayFragment(activity);
		}
		return instance;
	}

	// singleton pattern
	private ContentDisplayFragment(CmmActivity activity) {
		super();
		this.activity = activity;
	}

	/**
	 * Called on startup
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.content_display_fragment,
				container, false);

		this.ui_view = view;

		setupComponents();
		handleEvents();
		doLayout();

		return view;
	}

	/**
	 * Cleanup, important for video because closing the app will not stop the
	 * video from playing
	 */
	public void cleanup() {
		if (picture_fragment != null) {
			picture_fragment.disable();
		}

		if (video_fragment != null) {
			video_fragment.disable();
		}
	}

	/**
	 * Display Image
	 * 
	 * @param image
	 */
	public void displayImage(Drawable image) {
		this.displayImage(image, false);
	}

	public void displayImage(Drawable image, boolean forceRedisplay) {
		handleButtonState();
		if (cur_content != Content.PICTURE || forceRedisplay) {
			disableAllFragment(Content.PICTURE);
			picture_fragment.enable();
			cur_content = Content.PICTURE;
		}

		// Running out of memory

		/*
		 * Bitmap bitmap = ((BitmapDrawable) image).getBitmap(); int h0 =
		 * bitmap.getHeight(); int w0 = bitmap.getWidth(); int height =
		 * ui_view.getHeight(); int width = ui_view.getWidth(); double w_ratio =
		 * 1.0 * width / w0; double h_ratio = 1.0 * height / h0; if (h_ratio <
		 * w_ratio) { width = (int) (h_ratio * w0); } else { height = (int)
		 * (w_ratio * h0); }
		 * 
		 * Log.d(TAG, "screen: h: " + ui_view.getHeight() + ", w: " +
		 * ui_view.getWidth()); Log.d(TAG, "image: h: " + h0 + ", w: " + w0);
		 * Log.d(TAG, "cur: h: " + height + ", w: " + width); Drawable d = new
		 * BitmapDrawable(ui_view.getResources(),
		 * Bitmap.createScaledBitmap(bitmap, width, height, true));
		 * picture_fragment.displayMedia(d);
		 */
		picture_fragment.displayMedia(image);

	}

	/**
	 * Display Video
	 * 
	 * @param link
	 */
	public void displayVideo(String link) {
		displayVideo(link, false);
	}

	public void displayVideo(String link, boolean forceRedisplay) {
		handleButtonState();
		if (cur_content != Content.VIDEO || forceRedisplay) {
			disableAllFragment(Content.VIDEO);
			video_fragment.enable();

			cur_content = Content.VIDEO;
		}

		video_fragment.displayVideo(link);

	}

	/**
	 * Full screen image
	 * 
	 * @param image
	 */
	public void displayFullImage(Drawable image) {
		handleButtonState();
		Intent intent = new Intent(activity, PictureFullActivity.class);

		Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();

		intent.putExtra(PictureFullActivity.PIC_TAG, b);
		startActivityForResult(intent, 2);
	}

	/**
	 * Full screen video
	 * 
	 * @param url
	 */
	public void displayFullVideo(String url) {
		Intent i = new Intent(activity.getApplicationContext(),
				VideoFullPage.class);
		i.putExtra("URL", url);
		startActivityForResult(i, 2);
	}

	// disable buttons which are next/prev and full
	public void disableButtons() {
		ui_nextButton.setEnabled(false);
		ui_prevButton.setEnabled(false);
		ui_fullButton.setEnabled(false);
	}

	// enable buttons which are next/prev and full
	public void EnableButtons() {
		ui_nextButton.setEnabled(true);
		ui_prevButton.setEnabled(true);
		ui_fullButton.setEnabled(true);
	}

	// Start different animation depending on different visibility
	private void handleButtonState() {
		if (this.ui_nextButton.getVisibility() == View.VISIBLE) {
			ui_nextButton.startAnimation(anim_fadeoutButton);
		}

		if (this.ui_prevButton.getVisibility() == View.VISIBLE) {
			ui_prevButton.startAnimation(anim_fadeoutButton);
		}

		if (this.ui_fullButton.getVisibility() == View.VISIBLE) {
			ui_fullButton.startAnimation(anim_fadeoutButton);
		}
	}

	// show the buttons for pressing
	public void showButton() {
		this.ui_nextButton.setBackgroundResource(R.drawable.next_button);
		this.ui_prevButton.setBackgroundResource(R.drawable.prev_button);
		this.ui_fullButton.setBackgroundResource(R.drawable.fullscreen_button);
	}

	// Only show the full screen button for pressing
	public void showFullButton() {
		this.ui_fullButton.setBackgroundResource(R.drawable.fullscreen_button);
	}

	// Hide the buttons
	public void hideButtons() {
		this.ui_nextButton.setBackgroundColor(Color.TRANSPARENT);
		this.ui_prevButton.setBackgroundColor(Color.TRANSPARENT);
		this.ui_fullButton.setBackgroundColor(Color.TRANSPARENT);
	}

	// Assigning pointers to actual views/components in xml
	private void setupComponents() {
		FragmentTransaction fragmentTransaction = this.getFragmentManager()
				.beginTransaction();

		picture_fragment = PictureDisplayFragment.getInstance();
		fragmentTransaction.add(R.id.picture_fragment,
				(Fragment) picture_fragment);

		video_fragment = VideoDisplayFragment.getInstance(activity);
		fragmentTransaction.add(R.id.video_fragment, (Fragment) video_fragment);

		fragmentTransaction.commit();

		// UI Setup
		this.ui_nextButton = (Button) ui_view.findViewById(R.id.next_button);
		this.ui_prevButton = (Button) ui_view.findViewById(R.id.prev_button);
		this.ui_fullButton = (Button) ui_view
				.findViewById(R.id.fullscreen_button);

		// Animation setup
		anim_fadeoutButton = AnimationUtils.loadAnimation(ui_view.getContext(),
				R.anim.button_hide);
	}

	// Handle animation events
	private void handleEvents() {
		anim_fadeoutButton.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				hideButtons();
			}
		});
	}

	// Do Layout
	private void doLayout() {
		hideButtons();
		this.cleanup();
	}

	// Disable the fragment on quit
	private void disableAllFragment(Content exception) {
		if (exception != Content.PICTURE && picture_fragment != null) {
			picture_fragment.disable();
		}

		if (exception != Content.VIDEO && video_fragment != null) {
			video_fragment.disable();
		}
	}
}
