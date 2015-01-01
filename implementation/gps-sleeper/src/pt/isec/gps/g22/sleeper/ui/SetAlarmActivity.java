package pt.isec.gps.g22.sleeper.ui;

import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.Duration;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.SleepQuality;
import pt.isec.gps.g22.sleeper.core.time.DateTime;
import pt.isec.gps.g22.sleeper.core.time.TimeDelta;
import pt.isec.gps.g22.sleeper.core.time.TimeOfDay;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAOImpl;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
		
		hideActionBar();
	}
	
	public void setAlarm(View view) {
		AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmReceiver.class);
        
        final TimeOfDay wakeupTime = TimeOfDay.at(tpWakeHour.getCurrentHour(), tpWakeHour.getCurrentMinute());
        final DateTime now = DateTime.now();
        final TimeOfDay nowTime = now.toTimeOfDay();
        
        final DateTime wakeup;
        if (wakeupTime.compareTo(nowTime) > 0) {
        	wakeup = DateTime.fromDateTime(now, wakeupTime);
        } else {
        	final DateTime tomorrow = now.add(TimeDelta.duration(24));
        	wakeup = DateTime.fromDateTime(tomorrow, wakeupTime);
        }

        String msg = "There is a overlapped record!";
        dayRecordImp = new DayRecordDAOImpl(this);
		List<DayRecord> tempList = dayRecordImp.getAllRecords();
		
        dayRecord = new DayRecord(now.toUnixTimestamp(), wakeup.toUnixTimestamp());
		dayRecord.setExhaustion((int)rbExhaustion.getRating());
		dayRecord.setSleepQuality(SleepQuality.MEDIUM.getLevel());
			
		if(!dayRecord.recordOverlap(tempList)){
			dayRecord.setId(dayRecordImp.insertRecord(dayRecord));
		
			//send id record to change the record on dismiss alarm
			intent.putExtra("idDayRecord", dayRecord.getId());
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			//Set alarm based on calendar
			alarmMgr.set(AlarmManager.RTC_WAKEUP, wakeup.toMillis(), pendingIntent);
			msg = "Set alarm: " + wakeup;
		}
		
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        
		finish();
    }
	
    private void hideActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }
	
}


