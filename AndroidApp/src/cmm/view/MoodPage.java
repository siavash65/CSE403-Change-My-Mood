package cmm.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MoodPage extends Activity implements OnClickListener{
	private Button makeMeLaugh;
	private Button pumpMeUp;
	private Button inspireMe;
	private Button needSomeRomance;	
	private static int signin;
	private final String ABOUTUS = "About Us";
	private final String CONTACTUS = "Contact Us";
	private final String SIGNIN ="Sign in";
	private final String MOOD = "Mood";
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moodpage);
       
        Bundle bundle = getIntent().getExtras();
        signin = bundle.getInt(SIGNIN);
        Log.d("Extra data", signin+"");
       
        
        makeMeLaugh = (Button)findViewById(R.id.MakeMeLaugh);
        pumpMeUp = (Button)findViewById(R.id.PumpMeUp);
        inspireMe = (Button)findViewById(R.id.InspireMe);
        needSomeRomance = (Button)findViewById(R.id.NeedSomeRomance);
        
        makeMeLaugh.setOnClickListener(this);
        pumpMeUp.setOnClickListener(this);
        inspireMe.setOnClickListener(this);
        needSomeRomance.setOnClickListener(this);
        
    }

	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClass(this, MediaPage.class);
		int id = v.getId();
		if(id==R.id.MakeMeLaugh) {
			intent.putExtra(MOOD, 0);
		} else if(id==R.id.PumpMeUp) {
			intent.putExtra(MOOD, 1);
		} else if(id==R.id.InspireMe) {
			intent.putExtra(MOOD, 2);
		} else if(id==R.id.NeedSomeRomance) {
			intent.putExtra(MOOD, 3);
		}
		startActivity(intent);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(ABOUTUS);
        menu.add(CONTACTUS);
        return true;
    }
    
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = new Intent();
    	if(item.getTitle().equals(ABOUTUS)) {
    		intent.setClass(this, AboutUs.class);
    		startActivity(intent);
    		return true;
    	} else if (item.getTitle().equals(CONTACTUS)) {
    		intent.setClass(this, ContactUs.class);
    		startActivity(intent);
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
}
