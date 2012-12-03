package cmm.view;

import java.util.Stack;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

public class CMMActivity extends Activity{
	private ImageView imageView;
	int i=0;
	int imgid[] = {R.drawable.background1, R.drawable.background2, R.drawable.background3, R.drawable.background4, R.drawable.background5,
			R.drawable.background6, R.drawable.background7, R.drawable.background8, R.drawable.background9, 
			R.drawable.background10, R.drawable.background11, R.drawable.background12, R.drawable.background13,
			R.drawable.background14, R.drawable.background15, R.drawable.background16};
	
	RefreshHandler refreshHandler = new RefreshHandler();
	
	class RefreshHandler extends Handler {
		boolean is_paused = false;
		Stack<Message> s = new Stack<Message>();
		
		public synchronized void pause() {
			is_paused = true;
		}
		
		public synchronized void resume() {
			is_paused = false;
			while(!s.empty())
					sendMessageAtFrontOfQueue(s.pop());
		}
		
		@Override
		public void handleMessage(Message msg) {
			if(is_paused) {
				s.push(Message.obtain(msg));
				return;
			}
				
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
        changeBackground();        
    }
    

    public void onPause() {
    	super.onPause();
    	refreshHandler.pause();
    }
    
    public void onResume() {
    	super.onResume();
    	refreshHandler.resume();    	
    }
    
    public void onDestory() {
    	super.onDestroy();
    	refreshHandler.removeMessages(0);
    }
    
	public void skip_signin(View view){
		//Intent intent = new Intent(this, MoodPage.class);
		//FacebookHandler.getInstance().skipFB();
		//startActivity(intent);
	}
	
	public void facebookSignin(View view){
		//FacebookHandler.getInstance().doSignin(this, getBaseContext());
	}
}
