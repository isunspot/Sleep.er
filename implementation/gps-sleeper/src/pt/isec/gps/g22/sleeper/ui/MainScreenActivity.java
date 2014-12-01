package pt.isec.gps.g22.sleeper.ui;

import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.dal.ProfileDAOImpl;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainScreenActivity extends Activity {

	ProfileDAOImpl profileDAOImpl;
	Button btUserProfile, btSetAlarm, btWeeklyView, btSleepTips;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
		btUserProfile = (Button) findViewById(R.id.btUserProfile);
		btSetAlarm = (Button) findViewById(R.id.btSetAlarm);
		btWeeklyView = (Button) findViewById(R.id.btWeeklyView);
		btSleepTips = (Button) findViewById(R.id.btSleepTips);
		
		profileDAOImpl = new ProfileDAOImpl(MainScreenActivity.this);
		Profile profile = profileDAOImpl.loadProfile();
		
		if(profile == null) {
			Intent intent = new Intent(MainScreenActivity.this,ProfileActivity.class);
			intent.putExtra("isDefined",false);
			startActivity(intent);
		}
		        
		btUserProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(MainScreenActivity.this, ProfileActivity.class);
				intent.putExtra("isDefined",true);
				startActivity(intent);
			}
		});
		
		btSetAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent=new Intent(MainScreenActivity.this, SetAlarm.class);
				//startActivity(intent);
			}
		});
		
		btWeeklyView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent intent=new Intent(MainScreenActivity.this, WeeklyView.class);
				//startActivity(intent);
			}
		});
		
		btSleepTips.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(MainScreenActivity.this, SleepTips.class);
				startActivity(intent);
			}
		});
	}
}
