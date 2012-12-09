package cmm.view.newview.contentinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cmm.view.R;

/**
 * This class handles the construction of showing rating ups/downs
 * @author hunlan/Joon
 *
 */
public class ContentInfoFragment extends Fragment {
	private static final String TAG = "ContentInfoFragment";
	
	private View ui_view;
	private TextView[] ui_textviews;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.content_info_fragment,
				container, false);
		
		this.ui_view = view;
		ui_textviews = new TextView[] {
							(TextView) view.findViewById(R.id.ups_number),
							(TextView) view.findViewById(R.id.downs_number)};
		
		return view;
	}
	
	public void setText(String up_info, String down_info) {
		Log.d(TAG, "Setting up: " + up_info + ", down: " + down_info);
		ui_textviews[0].setText("Up: " + up_info);
		ui_textviews[1].setText("Down: " + down_info);
	}
}
