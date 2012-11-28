package cmm.view.newview.navigation;

import cmm.view.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NavigationFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("ABC", "lakdfjlakdsjflkjadsf");
		View view = inflater.inflate(R.layout.navigation_fragment, container,
				false);
		return view;
	}

}
