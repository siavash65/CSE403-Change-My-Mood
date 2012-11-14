package cmm.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import cmm.model.FacebookHandler;

public class CMMActivity extends Activity{
	private ImageView imageView;
	int i=0;
	int height;
	int imgid[] = {R.drawable.background1, R.drawable.background2, R.drawable.background3, R.drawable.background4, R.drawable.background5,
			R.drawable.background6, R.drawable.background7, R.drawable.background8, R.drawable.background9, 
			R.drawable.background10, R.drawable.background11, R.drawable.background12, R.drawable.background13,
			R.drawable.background14, R.drawable.background15, R.drawable.background16};
	
	RefreshHandler refreshHandler = new RefreshHandler();
	
	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			CMMActivity.this.changeBackground();
		}
		
		public void sleep(long delayMillis){
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}		
	};
	
	public void changeBackground(){
		refreshHandler.sleep(2000);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setImageResource(imgid[i]);
		i++;
		if(i==imgid.length)
			i = 0;
    }
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.imageView = (ImageView)this.findViewById(R.id.background_image);
        imageView.setAlpha(127);
        Display dis = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        height = dis.getHeight();
        changeBackground();        
    }
    
	public void skip_signin(View view){
		Intent intent = new Intent(this, MoodPage.class);
		FacebookHandler.getInstance().skipFB();
		startActivity(intent);
	}
	
	public void facebookSignin(View view){
		FacebookHandler.getInstance().doSignin(this, getBaseContext());
	}
}
