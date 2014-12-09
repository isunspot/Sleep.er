package pt.isec.gps.g22.sleeper.ui;

import pt.isec.gps.g22.sleeper.core.SleeperApp;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainScreenActivity extends Activity {
	
	SleeperApp sleeper;
	LinearLayout layoutSetAlarm,layoutWeeklyView,layoutGenericTips,layoutProfile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
		hideActionBar();
		
		sleeper = (SleeperApp)getApplication();
		sleeper.loadProfile();
        layoutSetAlarm = (LinearLayout)findViewById(R.id.LayoutSetAlarm);
        layoutWeeklyView = (LinearLayout)findViewById(R.id.LayoutWeeklyView);
        layoutGenericTips = (LinearLayout)findViewById(R.id.LayoutGenericTips);
        layoutProfile = (LinearLayout)findViewById(R.id.LayoutProfile);
		
		if(!sleeper.profileDefined()) {
			Intent intent = new Intent(MainScreenActivity.this,ProfileActivity.class);
			startActivity(intent);
		}
		     
		layoutSetAlarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(sleeper.profileDefined()) {
            		Intent intent = new Intent(MainScreenActivity.this, SetAlarmActivity.class);
            		startActivity(intent);
            	} else {
            		profileNotDefinedToast();
            	}
            }
		});
		
		layoutWeeklyView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	if(sleeper.profileDefined()) {
            		Intent intent = new Intent(MainScreenActivity.this, WeeklyViewActivity.class);
            		startActivity(intent);
            	} else {
            		profileNotDefinedToast();
            	}
            }
		});
		
		layoutGenericTips.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(MainScreenActivity.this, GenericTips.class);
				startActivity(intent);
            }
		});
		
		layoutProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(MainScreenActivity.this, ProfileActivity.class);
				startActivity(intent);
            }
		});
	}
	
    private void hideActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }
    
    private void profileNotDefinedToast() {
    	Toast.makeText(MainScreenActivity.this,"Profile not defined!",Toast.LENGTH_SHORT).show();
    }
}
