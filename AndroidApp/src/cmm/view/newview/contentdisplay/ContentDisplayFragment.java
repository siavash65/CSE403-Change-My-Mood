package cmm.view.newview.contentdisplay;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import cmm.model.Content;
import cmm.view.R;
import cmm.view.newview.CmmActivity;

public class ContentDisplayFragment extends Fragment {
	private static final String TAG = "ContentDisplayFragment";

	private static ContentDisplayFragment instance;

	private CmmActivity activity;
	
	private View ui_view;
	private Content cur_content;
	private PictureDisplayFragment picture_fragment;
	private VideoDisplayFragment video_fragment;
	
	private Button ui_nextButton;
	private Button ui_prevButton;
	
	private Animation anim_fadeoutButton;

	public static ContentDisplayFragment getInstance(CmmActivity activity) {
		if (instance == null) {
			instance = new ContentDisplayFragment(activity);
		}
		return instance;
	}

	private ContentDisplayFragment(CmmActivity activity) {
		super();
		this.activity = activity;
	}

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

	public void cleanup() {
		if (picture_fragment != null) {
			picture_fragment.disable();
		}

		if (video_fragment != null) {
			video_fragment.disable();
		}
	}

	public void displayImage(Drawable image) {
		handleButtonState();
		if (cur_content != Content.PICTURE) {
			disableAllFragment(Content.PICTURE);
			picture_fragment.enable();
			cur_content = Content.PICTURE;
		}
		
		// Running out of memory

		/*Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
		int h0 = bitmap.getHeight();
		int w0 = bitmap.getWidth();
		int height = ui_view.getHeight();
		int width = ui_view.getWidth();
		double w_ratio = 1.0 * width / w0;
		double h_ratio = 1.0 * height / h0;
		if (h_ratio < w_ratio) {
			width = (int) (h_ratio * w0);
		} else {
			height = (int) (w_ratio * h0);
		}

		Log.d(TAG,
				"screen: h: " + ui_view.getHeight() + ", w: "
						+ ui_view.getWidth());
		Log.d(TAG, "image: h: " + h0 + ", w: " + w0);
		Log.d(TAG, "cur: h: " + height + ", w: " + width);
		Drawable d = new BitmapDrawable(ui_view.getResources(),
				Bitmap.createScaledBitmap(bitmap, width, height, true));
		picture_fragment.displayMedia(d);*/
		picture_fragment.displayMedia(image);

	}

	public void displayVideo(String link) {
		handleButtonState();
		if (cur_content != Content.VIDEO) {
			disableAllFragment(Content.VIDEO);
			video_fragment.enable();

			cur_content = Content.VIDEO;
		}

		video_fragment.displayVideo(link);

	}
	
	public void disableButtons() {
		ui_nextButton.setEnabled(false);
		ui_prevButton.setEnabled(false);
	}
	
	public void EnableButtons() {
		ui_nextButton.setEnabled(true);
		ui_prevButton.setEnabled(true);
	}
	
	private void handleButtonState() {
		if (this.ui_nextButton.getVisibility() == View.VISIBLE) {
			ui_nextButton.startAnimation(anim_fadeoutButton);
			ui_prevButton.startAnimation(anim_fadeoutButton);
		}
	}
	
	public void showButton() {
		this.ui_nextButton.setBackgroundResource(R.drawable.next_button);
		this.ui_prevButton.setBackgroundResource(R.drawable.prev_button);
	}
	
	public void hideButtons() {
		this.ui_nextButton.setBackgroundColor(Color.TRANSPARENT);
		this.ui_prevButton.setBackgroundColor(Color.TRANSPARENT);
	}
	
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
		
		// Animation setup
		anim_fadeoutButton = AnimationUtils.loadAnimation(ui_view.getContext(), R.anim.button_hide);
	}

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

	private void doLayout() {
		hideButtons();
		this.cleanup();
	}

	private void disableAllFragment(Content exception) {
		if (exception != Content.PICTURE && picture_fragment != null) {
			picture_fragment.disable();
		}

		if (exception != Content.VIDEO && video_fragment != null) {
			video_fragment.disable();
		}
	}
}
