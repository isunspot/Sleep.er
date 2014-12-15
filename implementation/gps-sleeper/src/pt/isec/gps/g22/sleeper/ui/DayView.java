package pt.isec.gps.g22.sleeper.ui;

import static pt.isec.gps.g22.sleeper.core.time.TimeUtils.days;

import java.util.ArrayList;
import java.util.Calendar;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.SleeperApp;
import pt.isec.gps.g22.sleeper.core.time.DateTime;
import pt.isec.gps.g22.sleeper.core.time.TimeDelta;
import pt.isec.gps.g22.sleeper.core.time.TimeUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class DayView extends Activity {

	DateTime day;
	TextView tvDay,tvInsert;
	ListView dayViewList;
	CustomAdapter customAdapter;
	SleeperApp sleeper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day_view);
		
		sleeper = (SleeperApp) getApplication();
		
		Intent intent = getIntent();		
		final long daySeconds = intent.getLongExtra("day", -1);
		day = DateTime.fromSeconds(daySeconds);
		
		tvDay = (TextView) findViewById(R.id.tvDay);
	    dayViewList = (ListView) findViewById(R.id.DayViewList);	

	    tvDay.setText(TimeUtils.formatDate(day));
	    tvInsert = (TextView) findViewById(R.id.tvInsert);
	    hideActionBar();
	    setInfo();
	    
	    tvInsert.setOnClickListener(new OnClickListener() {
	    	@Override
			public void onClick(View v) {
				Intent intent = new Intent(DayView.this, DailyRecordActivity.class);
				intent.putExtra("day", day.toUnixTimestamp());
				startActivity(intent);
			}
	    });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		setInfo();
	}
	
	private void setInfo() {
		customAdapter = new CustomAdapter(
				getApplicationContext(),
				sleeper,
				day,
				sleeper.getDayRecordDAO().getRecords(
						day.toUnixTimestamp(), 
						day.add(days(1)).toUnixTimestamp()));
		dayViewList.setAdapter(customAdapter);
		dayViewList.invalidate();
	}
	
    private void hideActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }
	
}
