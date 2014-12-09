package pt.isec.gps.g22.sleeper.ui;

import pt.isec.gps.g22.sleeper.dal.ProfileDAOImpl;
import pt.isec.gps.g22.sleeper.ui.SleeperApp;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainScreenActivity extends Activity {
	
	SleeperApp sleeper;
	Button btUserProfile, btSetAlarm, btWeeklyView, btSleepTips;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
		sleeper = (SleeperApp)getApplication();
		sleeper.loadProfile();
		
		btUserProfile = (Button) findViewById(R.id.btUserProfile);
		btSetAlarm = (Button) findViewById(R.id.btSetAlarm);
		btWeeklyView = (Button) findViewById(R.id.btWeeklyView);
		btSleepTips = (Button) findViewById(R.id.btSleepTips);
		
		if(!sleeper.profileDefined()) {
			Intent intent = new Intent(MainScreenActivity.this,ProfileActivity.class);
			startActivity(intent);
		}
		        
		btUserProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(MainScreenActivity.this, ProfileActivity.class);
				startActivity(intent);
			}
		});
		
		btSetAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(MainScreenActivity.this, SetAlarmActivity.class);
				startActivity(intent);
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
				Intent intent=new Intent(MainScreenActivity.this, GenericTips.class);
				startActivity(intent);
			}
		});
	}
}
