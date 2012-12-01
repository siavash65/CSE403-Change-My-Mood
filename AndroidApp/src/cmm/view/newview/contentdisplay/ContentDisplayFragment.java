package cmm.view.newview.contentdisplay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cmm.model.Content;
import cmm.view.R;

public class ContentDisplayFragment extends Fragment {
	private static final String TAG = "ContentDisplayFragment";

	private static ContentDisplayFragment instance;

	private Activity activity;

	private View ui_view;
	private Content cur_content;
	private PictureDisplayFragment picture_fragment;
	private VideoDisplayFragment video_fragment;

	public static ContentDisplayFragment getInstance(Activity activity) {
		if (instance == null) {
			instance = new ContentDisplayFragment(activity);
		}
		return instance;
	}

	private ContentDisplayFragment(Activity activity) {
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
		if (cur_content != Content.PICTURE) {
			disableAllFragment(Content.PICTURE);
			picture_fragment.enable();
			cur_content = Content.PICTURE;
		}

		Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
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
		picture_fragment.displayMedia(d);

	}

	public void displayVideo(String link) {
		if (cur_content != Content.VIDEO) {
			disableAllFragment(Content.VIDEO);
			video_fragment.enable();

			cur_content = Content.VIDEO;
		}

		video_fragment.displayVideo(link);

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
	}

	private void handleEvents() {

	}

	private void doLayout() {

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
