package cmm.view.newview.navigation;

import java.util.Collection;
import java.util.HashSet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cmm.model.Content;
import cmm.model.Mood;
import cmm.view.R;
import cmm.view.newview.CmmActivity;

public class ContentSelectionFragment extends Fragment {
	private static final String TAG = "ContentSelectionFragment";
	
	private static ContentSelectionFragment instance;

	private CmmActivity activity;

	// views
	private View ui_view;
	private ViewGroup ui_toplevel;
	private Collection<Button> ui_contentButtons;
	private Collection<Button> ui_bars;
	private Collection<RelativeLayout> ui_buttonLayouts;
	private Button cur_content;

	// animation
	private Animation anim_fadeout;
	private Animation anim_scale;

	private Animation anim_fadein;
	private Animation anim_scaleback;

	// model
	private Mood mood;
	private Content content;
	private boolean displayingContent;

	/**
	 * Get instance method
	 * 
	 * @param mood
	 * @return
	 */
	public static ContentSelectionFragment getInstance(CmmActivity activity,
			Mood mood) {
		if (instance == null) {
			instance = new ContentSelectionFragment(activity, mood);
		}
		return instance;
	}

	/**
	 * private constructor
	 * 
	 * @param mood
	 */
	private ContentSelectionFragment(CmmActivity activity, Mood mood) {
		this.mood = mood;
		this.activity = activity;
		this.displayingContent = false;
	}

	public void setMood(Mood mood) {
		this.mood = mood;
	}

	public Content getContent() {
		return this.content;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.content_fragment, container,
				false);

		this.ui_view = view;

		setupComponents();
		handleEvents();
		doLayout();

		// Show content
		ui_toplevel.setVisibility(View.VISIBLE);
		doAnimation();

		return view;
	}

	public void contentClick(View view) {
		ui_view.setBackgroundColor(Color.TRANSPARENT);

		// hide button
		for (Button b : ui_contentButtons) {
			b.setEnabled(false);
			if (view == b) {
				if (b.getId() == R.id.picture_button) {
					b.setBackgroundResource(R.drawable.content_picture_button_select);
				} else if (b.getId() == R.id.video_button) {
					b.setBackgroundResource(R.drawable.content_video_button_select);
				}
			} else {
				if (b.getId() == R.id.picture_button) {
					b.setBackgroundResource(R.drawable.content_picture_button_default);
				} else if (b.getId() == R.id.video_button) {
					b.setBackgroundResource(R.drawable.content_video_button_default);
				}
			}
			b.startAnimation(anim_fadeout);
		}

		if (cur_content == null || cur_content != view) {
			// If no vid/pic set or selected different content
			if (cur_content != null) {
				Log.d(TAG, "deseletected");
				this.cur_content
						.setBackgroundResource(R.drawable.sign_deselect);
			}

			// Change the sign color
			if (view.getId() == R.id.picture_button) {
				Button pic_button = (Button) ui_view
						.findViewById(R.id.picture_sign);
				pic_button.setBackgroundResource(R.drawable.sign_select);
				this.cur_content = pic_button;
				this.content = Content.PICTURE;
			} else if (view.getId() == R.id.video_button) {
				Button vid_button = (Button) ui_view
						.findViewById(R.id.video_sign);
				vid_button.setBackgroundResource(R.drawable.sign_select);
				this.cur_content = vid_button;
				this.content = Content.VIDEO;
			}
		}

		Toast.makeText(view.getContext(), "mood = " + mood.value,
				Toast.LENGTH_SHORT).show();
		// Display Content
		if (view.getId() == R.id.picture_button) {
			activity.displayNextImage(mood);
		} else if (view.getId() == R.id.video_button) {
			activity.displayNextVideo(mood);
		}
	}

	/**
	 * After button fadded out
	 */
	private void afterFadeOutEnd() {
		for (Button b : ui_contentButtons) {
			// hide button
			b.setVisibility(View.INVISIBLE);
		}
		ui_toplevel.startAnimation(anim_scale);  // TODO: this is doing twice
	}

	private void afterScaleEnd() {
		for (Button b : ui_contentButtons) {
			b.setVisibility(View.GONE);
			b.setEnabled(true);
		}
		displayingContent = false;
	}

	public void reselect() {
		if (!displayingContent) {
			ui_view.setBackgroundResource(R.drawable.sign_bg_select);
			for (Button b : ui_contentButtons) {
				b.setVisibility(View.VISIBLE);
			}
			ui_toplevel.startAnimation(anim_scaleback);
			displayingContent = true;
		} else {
			for (Button b : ui_contentButtons) {
				b.setEnabled(false);
			}
			ui_view.setBackgroundColor(Color.TRANSPARENT);
			ui_toplevel.startAnimation(anim_scale);
		}

	}

	public void newselect() {
		ui_view.setBackgroundResource(R.drawable.sign_bg_select);
		for (Button b : ui_contentButtons) {
			b.setVisibility(View.VISIBLE);
		}
		ui_toplevel.startAnimation(anim_scaleback);
		displayingContent = true;
	}

	/*
	 * Setup the ui components
	 */
	private void setupComponents() {
		//
		ui_toplevel = (ViewGroup) ui_view
				.findViewById(R.id.content_fragment_main);

		// setup content buttons
		ui_contentButtons = new HashSet<Button>();
		ui_contentButtons.add((Button) ui_view
				.findViewById(R.id.picture_button));
		ui_contentButtons.add((Button) ui_view.findViewById(R.id.video_button));

		// setup content bars
		ui_bars = new HashSet<Button>();
		ui_bars.add((Button) ui_view.findViewById(R.id.picture_sign));
		ui_bars.add((Button) ui_view.findViewById(R.id.video_sign));

		// setup layouts
		ui_buttonLayouts = new HashSet<RelativeLayout>();
		ui_buttonLayouts.add((RelativeLayout) ui_view
				.findViewById(R.id.picturebutton_layout));
		ui_buttonLayouts.add((RelativeLayout) ui_view
				.findViewById(R.id.videobutton_layout));

		// setup animation
		anim_fadeout = AnimationUtils.loadAnimation(ui_view.getContext(),
				R.anim.content_hide);
		anim_scale = AnimationUtils.loadAnimation(ui_view.getContext(),
				R.anim.content_layout_scale);
		anim_fadein = AnimationUtils.loadAnimation(ui_view.getContext(),
				R.anim.content_show);
		anim_scaleback = AnimationUtils.loadAnimation(ui_view.getContext(),
				R.anim.content_layout_scaleback);
	}

	/*
	 * Add listeners if necessary
	 */
	private void handleEvents() {
		// Button click event
		for (Button b : ui_contentButtons) {
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					contentClick(v);
				}
			});
		}

		// Animation call back - Fade out = button exit
		anim_fadeout.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				afterFadeOutEnd();
			}
		});

		// Animation call back
		anim_scale.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				afterScaleEnd();
			}
		});
		
		// Show image
		anim_scaleback.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				for (Button b : ui_contentButtons) {
					b.setEnabled(true);
				}
			}
		});
	}

	/*
	 * Resize layout if necessary
	 */
	private void doLayout() {

	}

	private void doAnimation() {
		ui_toplevel.startAnimation(anim_scaleback);
		displayingContent = true;
	}
}
