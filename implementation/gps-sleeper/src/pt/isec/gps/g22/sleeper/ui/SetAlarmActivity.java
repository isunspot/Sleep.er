package pt.isec.gps.g22.sleeper.ui;

import java.util.Calendar;

import javax.xml.datatype.Duration;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.SleepQuality;
import pt.isec.gps.g22.sleeper.core.time.DateTime;
import pt.isec.gps.g22.sleeper.core.time.TimeDelta;
import pt.isec.gps.g22.sleeper.core.time.TimeOfDay;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAOImpl;
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
	}
	
	public void setAlarm(View view) {
		AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmReceiver.class);
        
        final TimeOfDay wakeupTime = TimeOfDay.at(tpWakeHour.getCurrentHour(), tpWakeHour.getCurrentMinute());
        final DateTime now = DateTime.now();
        final TimeOfDay nowTime = now.toTimeOfDay();
        
        final DateTime wakeup;
        if (wakeupTime.compareTo(nowTime) > 0) {
        	wakeup = DateTime.fromDateTime(now.getYear(), now.getMonth(), now.getDay(), wakeupTime.getHours(), wakeupTime.getMinutes(), 0);
        } else {
        	final DateTime tomorrow = now.add(TimeDelta.duration(24));
        	wakeup = DateTime.fromDateTime(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDay(), wakeupTime.getHours(), wakeupTime.getMinutes(), 0);
        }

        dayRecord = new DayRecord(now.toUnixTimestamp(), wakeup.toUnixTimestamp());
		dayRecord.setExhaustion((int)rbExhaustion.getRating());
		dayRecord.setSleepQuality(SleepQuality.MEDIUM.getLevel());
				
		dayRecordImp = new DayRecordDAOImpl(this);
		dayRecord.setId(dayRecordImp.insertRecord(dayRecord));
		
		//send id record to change the record on dismiss alarm
		intent.putExtra("idDayRecord", dayRecord.getId());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		//Set alarm based on calendar
        alarmMgr.set(AlarmManager.RTC_WAKEUP, wakeup.toMillis(), pendingIntent);
        Toast.makeText(this, "Set alarm: " + wakeup, Toast.LENGTH_SHORT).show();
        
		finish();
    }
	
}


