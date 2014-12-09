package pt.isec.gps.g22.sleeper.ui;

import java.util.Calendar;
import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAOImpl;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;

public class DailyRecordActivity extends Activity {

	TimePicker tpSleep;
	TimePicker tpWake;
	RatingBar rbExhaustion;
	RatingBar rbQualitySleep;
	int idDayRecord;
	int dayTime;
	DayRecordDAOImpl dayRecordoDAOImp;
	DayRecord dayRecord;
	Boolean editMode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily_record);
		
		Intent intent = getIntent();		
		idDayRecord  = intent.getIntExtra("idDayRecord", -1);
		dayTime  = intent.getIntExtra("dayTime", -1);
		
		dayRecordoDAOImp = new DayRecordDAOImpl(getApplicationContext());
		
		tpSleep = (TimePicker) findViewById(R.id.tpSleepDaily);
		tpWake = (TimePicker) findViewById(R.id.tpWakeHourDaily);
		rbExhaustion = (RatingBar) findViewById(R.id.rbExhaustionDaily);
		rbQualitySleep = (RatingBar) findViewById(R.id.rbQualitySleepDaily);
		
		editMode = (idDayRecord != -1);
		
		if((editMode))
			populateFields(idDayRecord);
		else
		{
			tpSleep.setCurrentHour(11);
			tpSleep.setCurrentMinute(0);
			tpWake.setCurrentHour(8);
			tpWake.setCurrentMinute(0);	
		}
	}
	
	public void cancelDailyRecord(View view) {				       
		finish();
    }
	
	public void saveDailyRecord(View view) {				
		if(editMode)
		{
			dayRecord = setDayRecord(dayRecord);
			dayRecordoDAOImp.updateRecord(dayRecord);
		}
		else
		{
			dayRecord = setDayRecord(null);
			dayRecordoDAOImp.insertRecord(dayRecord);
		}
		      
		Toast.makeText(this, "Daily record saved", Toast.LENGTH_SHORT).show();
        
		finish();
    }
	
	
	private DayRecord setDayRecord(DayRecord dayRecord){
		DayRecord tempRecord;
		int hour;
		int min;
		
		if(dayRecord==null)
			tempRecord= new DayRecord();
		else
			tempRecord = dayRecord;
				
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dayTime);
		hour = tpSleep.getCurrentHour();
		min = tpSleep.getCurrentMinute();
               
		//Set calendar        
        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE,min);
        cal.set(Calendar.SECOND, 0); 
        
        
		tempRecord.setSleepDate((int)cal.getTimeInMillis()/1000);
		tempRecord.setExhaustion((int) rbExhaustion.getRating());
		
		hour = tpWake.getCurrentHour();
		min = tpWake.getCurrentMinute();
		
		//Set calendar
		if((hour <= cal.get(Calendar.HOUR_OF_DAY))&&(min <= cal.get(Calendar.MINUTE)))
        	hour+=24;
		
        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE,min);
        cal.set(Calendar.SECOND, 0);
		
		tempRecord.setWakeupDate((int)cal.getTimeInMillis()/1000);
		tempRecord.setSleepQuality((int) rbQualitySleep.getRating());
		
		return tempRecord;
	}
	
	private void populateFields(int idDayRecord){
		if(idDayRecord != -1)
		{			
			dayRecord = dayRecordoDAOImp.loadDayRecord(idDayRecord);
			if(dayRecord!=null)
			{
				Calendar calendar = Calendar.getInstance();
			    calendar.setTimeInMillis(dayRecord.getSleepDate()*1000);
			    
				tpSleep.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
				tpSleep.setCurrentMinute(calendar.get(Calendar.MINUTE));
				
				calendar.setTimeInMillis(dayRecord.getWakeupDate()*1000);
				tpWake.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
				tpWake.setCurrentMinute(calendar.get(Calendar.MINUTE));
			}
		}
	}
	
}
