package cmm.view.newview.contentdisplay;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cmm.view.R;

/**
 * This class handles the displaying of pictures
 * @author hunlan
 *
 */
public class PictureDisplayFragment extends Fragment {
	private View ui_view;
	private ImageView imageView;

	public PictureDisplayFragment() {
		super();
	}

	/**
	 * On startup
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("PictureDisplayFragment", "onCreateView");
		View view = inflater.inflate(R.layout.picture_fragment, container,
				false);
		this.ui_view = view;

		setupComponents();
		handleEvents();
		doLayout();

		return view;
	}

	// Display media
	public void displayMedia(Drawable image) {	
		ui_view.findViewById(R.id.picture_parent).setBackgroundColor(Color.BLACK);
		imageView.setImageDrawable(image);
	}
	
	// Remove media
	public void disable() {
		if (ui_view != null) {
			ui_view.setVisibility(View.GONE);
		}
	}
	
	public void enable() {
		ui_view.setVisibility(View.VISIBLE);
	}
 
	private void setupComponents() {
		this.imageView = (ImageView) ui_view.findViewById(R.id.imageView);
	}

	private void handleEvents() {

	}

	private void doLayout() {

	}
}
