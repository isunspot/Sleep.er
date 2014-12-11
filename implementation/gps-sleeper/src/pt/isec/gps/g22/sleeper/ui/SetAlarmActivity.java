package pt.isec.gps.g22.sleeper.ui;

import java.util.Calendar;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAOImpl;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;


public class SetAlarmActivity extends Activity {

	TimePicker tpWakeHour;
	RatingBar rbExhaustion;
	DayRecordDAOImpl dayRecordImp;
	DayRecord dayRecord;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_alarm);
		tpWakeHour = (TimePicker) findViewById(R.id.tpWakeHourDaily);
		rbExhaustion = (RatingBar) findViewById(R.id.rbExhaustionDaily);
	}
	
	public void setAlarm(View view) {
		AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmReceiver.class);
		
        
		Calendar cal = Calendar.getInstance();
		long millis = System.currentTimeMillis();
        cal.setTimeInMillis(millis);
        int hour = tpWakeHour.getCurrentHour();
        int min = tpWakeHour.getCurrentMinute();
        
        //Set calendar
        if((hour <= cal.get(Calendar.HOUR_OF_DAY))&&(min <= cal.get(Calendar.MINUTE)))
        	hour+=24;
        
        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE,min);
        cal.set(Calendar.SECOND, 0);    
		
        long sleepDate,wakeDate;
        sleepDate = millis / 1000;
        wakeDate = cal.getTimeInMillis() / 1000;
        //Insert daily record
		dayRecord = new DayRecord(sleepDate,wakeDate);
		dayRecord.setExhaustion((int)rbExhaustion.getRating());
		dayRecord.setSleepQuality(2);
				
		dayRecordImp = new DayRecordDAOImpl(this);
		dayRecord.setId(dayRecordImp.insertRecord(dayRecord));
		
		//send id record to change the record on dismiss alarm
		intent.putExtra("idDayRecord", dayRecord.getId());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//Set alarm based on calendar
        alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, "Set alarm:"+cal.getTime().toString(), Toast.LENGTH_SHORT).show();
        
		finish();
    }
	
}


