package pt.isec.gps.g22.sleeper.ui;

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

	int day;
	TextView tvDay,tvInsert;
	ListView dayViewList;
	CustomAdapter customAdapter;
	SleeperApp sleeper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_day_view);
		
		sleeper = (SleeperApp)getApplication();
		
		Intent intent = getIntent();		
		day = intent.getIntExtra("day", -1);
		
		tvDay = (TextView) findViewById(R.id.tvDay);
	    dayViewList = (ListView) findViewById(R.id.DayViewList);
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
	
	private void setInfo() {
	      CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(),sleeper,sleeper.getDayRecordDAO().getRecords(day, day+86400));
	      dayViewList.setAdapter(customAdapter);
	}
	
    private void hideActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }
	
}
