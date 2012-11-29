package cmm.view.newview.navigation;

import java.util.Collection;
import java.util.HashSet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import cmm.model.Mood;
import cmm.view.R;

public class ContentSelectionFragment extends Fragment {
	private static ContentSelectionFragment instance;

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
	private boolean displayingContent;

	/**
	 * Get instance method
	 * 
	 * @param mood
	 * @return
	 */
	public static ContentSelectionFragment getInstance(Mood mood) {
		if (instance == null || instance.mood != mood) {
			instance = new ContentSelectionFragment(mood);
		}
		return instance;
	}

	/**
	 * private constructor
	 * 
	 * @param mood
	 */
	private ContentSelectionFragment(Mood mood) {
		this.mood = mood;
		this.displayingContent = false;
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
			if (view == b) {
				if (b.getId() == R.id.picture_button) {
					b.setBackgroundResource(R.drawable.content_picture_button_select);
				} else if (b.getId() == R.id.video_button) {
					b.setBackgroundResource(R.drawable.content_video_button_select);
				}
			}
			b.startAnimation(anim_fadeout);
		}

		if (cur_content == null || cur_content != view) {
			// If no vid/pic set or selected different content
			if (cur_content != null) {
				this.cur_content
						.setBackgroundResource(R.drawable.sign_deselect);
			}

			// Change the sign color
			if (view.getId() == R.id.picture_button) {
				Button pic_button = (Button) ui_view
						.findViewById(R.id.picture_sign);
				pic_button.setBackgroundResource(R.drawable.sign_select);
				this.cur_content = pic_button;
			} else if (view.getId() == R.id.video_button) {
				Button vid_button = (Button) ui_view
						.findViewById(R.id.video_sign);
				vid_button.setBackgroundResource(R.drawable.sign_select);
				this.cur_content = vid_button;
			}
		}

		Toast.makeText(view.getContext(), "mood = " + mood.value,
				Toast.LENGTH_SHORT).show();
	}

	private void afterFadeOutEnd() {
		for (Button b : ui_contentButtons) {
			b.setVisibility(View.INVISIBLE);
		}
		ui_toplevel.startAnimation(anim_scale);
	}

	private void afterScaleEnd() {
		for (Button b : ui_contentButtons) {
			b.setVisibility(View.GONE);
		}
		displayingContent = false;
	}

	public void reselect() {
		ui_view.setBackgroundResource(R.drawable.sign_bg_select);
		for (Button b : ui_contentButtons) {
			b.setVisibility(View.VISIBLE);
		}
		if (!displayingContent) {
			ui_toplevel.startAnimation(anim_scaleback);
			displayingContent = true;
		}
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

		// Animation call back
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
