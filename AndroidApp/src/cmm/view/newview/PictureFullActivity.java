package cmm.view.newview;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import cmm.view.R;

public class PictureFullActivity extends Activity {
	private static final String TAG = "PictureFullActivity";
	
	public static final String PIC_TAG = "picture";
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

		this.setContentView(R.layout.picture_full);
		
		Bundle extras = getIntent().getExtras();
		byte[] b = extras.getByteArray(PIC_TAG);
		
		Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
		ImageView image = (ImageView) findViewById(R.id.full_image);
		
		image.setImageBitmap(bmp);
	}

	public void rotate(View view) {
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			Log.d(TAG, "port");
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			Log.d(TAG, "land");
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
}
