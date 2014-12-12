package pt.isec.gps.g22.sleeper.ui;

import java.util.ArrayList;
import java.util.Calendar;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.SleeperApp;
import pt.isec.gps.g22.sleeper.core.TimeUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class DayView extends Activity {

	long day;
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
		day = intent.getLongExtra("day", -1);
		
		tvDay = (TextView) findViewById(R.id.tvDay);
	    dayViewList = (ListView) findViewById(R.id.DayViewList);	
	    day = day * 1000;
	    tvDay.setText(TimeUtils.getDate(day));
	    tvInsert = (TextView) findViewById(R.id.tvInsert);
	    hideActionBar();
	    setInfo();
	    
	    tvInsert.setOnClickListener(new OnClickListener() {
	    	@Override
			public void onClick(View v) {
				Intent intent = new Intent(DayView.this,DailyRecordActivity.class);
				intent.putExtra("day", day);
				startActivity(intent);
			}
	    });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (customAdapter.listRecords != null) {
			customAdapter.refresAdapter(new ArrayList<DayRecord>(customAdapter.listRecords));
		}
	}
	
	private void setInfo() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		day = cal.getTimeInMillis()/1000;
	      CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),sleeper,sleeper.getDayRecordDAO().getRecords(day, day+86400));
	      dayViewList.setAdapter(customAdapter);
	}
	
    private void hideActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }
	
}
