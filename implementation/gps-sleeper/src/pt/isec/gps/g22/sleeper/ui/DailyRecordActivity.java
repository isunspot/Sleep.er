package pt.isec.gps.g22.sleeper.ui;

import java.util.Calendar;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.SleeperApp;
import pt.isec.gps.g22.sleeper.core.time.DateTime;
import pt.isec.gps.g22.sleeper.core.time.TimeDelta;
import pt.isec.gps.g22.sleeper.core.time.TimeOfDay;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class DailyRecordActivity extends Activity {

	SleeperApp sleeperApp;
	RatingBar rbExhaustion,rbQualitySleep;
	LinearLayout sleepHourLayout,wakeupHourLayout;
	TextView tvSleepHourValue,tvWakeupHourValue;
	int idDayRecord;
	DateTime day;
	DayRecord dayRecord;
	Boolean editMode;
//	int sleepHour, sleepMin, wakeupHour, wakeupMin;
	TimeOfDay sleep, wakeup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily_record);
		
		hideActionBar();
		
		sleeperApp = (SleeperApp)getApplication();
		
		Intent intent = getIntent();		
		idDayRecord  = intent.getIntExtra("idDayRecord", -1);
		final long daySeconds = intent.getLongExtra("day", -1);
		day = DateTime.fromSeconds(daySeconds);
				
		tvSleepHourValue = (TextView) findViewById(R.id.tvSleepHourValue);
		tvWakeupHourValue = (TextView) findViewById(R.id.tvWakeupHourValue);		
		sleepHourLayout = (LinearLayout) findViewById(R.id.SleepHourLayout);
		wakeupHourLayout = (LinearLayout) findViewById(R.id.WakeupHourLayout);
		rbExhaustion = (RatingBar) findViewById(R.id.rbExhaustionDaily);
		rbQualitySleep = (RatingBar) findViewById(R.id.rbSleepQualityDaily);

		editMode = (idDayRecord != -1);
		
		if(editMode)
			populateFields(idDayRecord);
		
	    sleepHourLayout.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	    	    DialogFragment newFragment = new TimePickerFragment(1);
	    	    newFragment.show(getFragmentManager(), "timePickerSH");
	        }
	    });
	    
	    wakeupHourLayout.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	    	    DialogFragment newFragment = new TimePickerFragment(2);
	    	    newFragment.show(getFragmentManager(), "timePickerWH");
	        }
	    });
	}
	
	public void saveDailyRecord(View view) {				
		if(editMode) {
			dayRecord = setDayRecord(dayRecord);
			sleeperApp.getDayRecordDAO().updateRecord(dayRecord);
		} else {
			dayRecord = setDayRecord(null);
			sleeperApp.getDayRecordDAO().insertRecord(dayRecord);
		}
		      
		Toast.makeText(this, "Daily record saved", Toast.LENGTH_SHORT).show();    
		finish();
    }
	
    private void hideActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }
	
	private DayRecord setDayRecord(DayRecord dayRecord){
		final DayRecord tempRecord = dayRecord == null ? new DayRecord() : dayRecord;
		
        final DateTime sleepTime = DateTime.fromDateTime(
        		day.getYear(),
        		day.getMonth(),
        		day.getDay(),
        		sleep.getHours(), 
        		sleep.getMinutes(), 0);
        
		tempRecord.setSleepDate(sleepTime.toUnixTimestamp());
		tempRecord.setExhaustion((int) rbExhaustion.getRating());
				
		DateTime wakeupTime = DateTime.fromDateTime(
				day.getYear(),
        		day.getMonth(),
        		day.getDay(),
        		wakeup.getHours(), 
        		wakeup.getMinutes(), 0);
		
		if (wakeup.compareTo(sleep) <= 0) {
			wakeupTime = wakeupTime.add(TimeDelta.duration(24));
		}
			
		tempRecord.setWakeupDate(wakeupTime.toUnixTimestamp());
		tempRecord.setSleepQuality((int) rbQualitySleep.getRating());
		
		return tempRecord;
	}
	
	private void populateFields(int idDayRecord){
		if(idDayRecord != -1)
		{			
			dayRecord = sleeperApp.getDayRecordDAO().loadDayRecord(idDayRecord);
			if(dayRecord != null)
			{
				final DateTime sleepDate = DateTime.fromSeconds(dayRecord.getSleepDate());
				sleep = TimeOfDay.at(sleepDate.getHours(), sleepDate.getMinutes(), 0); 
			    String sleepHour = (String) android.text.format.DateFormat.format("hh:mm", sleepDate.asCalendar().getTime());
			    tvSleepHourValue.setText(sleepHour);
			    
			    final DateTime wakeupDate = DateTime.fromSeconds(dayRecord.getWakeupDate());
			    sleep = TimeOfDay.at(wakeupDate.getHours(), wakeupDate.getMinutes(), 0);
			    String wakeupHour = (String) android.text.format.DateFormat.format("hh:mm", wakeupDate.asCalendar().getTime());
			    tvWakeupHourValue.setText(wakeupHour);
			}
		}
	}
	
    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    	int type;
    	public TimePickerFragment(int type) {
    		this.type = type;
    	}
    	
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		final Calendar c = Calendar.getInstance();
    		int hour, minute;
    		    		
    		if(type==1) {
    			if(dayRecord != null) {
    			    c.setTimeInMillis(dayRecord.getSleepDate()*1000);
    			} else {
    				c.set(Calendar.HOUR_OF_DAY, 23);
    				c.set(Calendar.MINUTE, 00);
    			}
    		} else {
        		if(type==2) {
        			if(dayRecord != null) {
        			    c.setTimeInMillis(dayRecord.getWakeupDate()*1000);
        			} else {
        				c.set(Calendar.HOUR_OF_DAY, 8);
        				c.set(Calendar.MINUTE, 00);
        			}
        		}
    		}
    		
			hour = c.get(Calendar.HOUR_OF_DAY);
			minute = c.get(Calendar.MINUTE);

    		return new TimePickerDialog(getActivity(), this, hour, minute,DateFormat.is24HourFormat(getActivity()));
    	}

    	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    		if(view.isShown()) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
				cal.set(Calendar.MINUTE, minute);
    			if(type == 1) {
			    	String sSleepHour = (String) android.text.format.DateFormat.format("hh:mm", cal.getTime());
			    	tvSleepHourValue.setText(sSleepHour);
//			    	sleepHour = hourOfDay;
//			    	sleepMin = minute;
			    	sleep = TimeOfDay.at(hourOfDay, minute, 0);
    			} else {
    				if(type == 2) {
    			    	String sWakeupHour = (String) android.text.format.DateFormat.format("hh:mm", cal.getTime());
    			    	tvWakeupHourValue.setText(sWakeupHour);
//    			    	wakeupHour = hourOfDay;
//    			    	wakeupMin = minute;
    			    	wakeup = TimeOfDay.at(hourOfDay, minute, 0);
    				}
    			}
    		}
    	}
    	
        @Override
        public void onStart() {
            super.onStart();
            final Resources res = getResources();
            final int color = Color.parseColor("#6627ae60");
            // Title
            final int titleId = res.getIdentifier("alertTitle", "id", "android");
            final View title = getDialog().findViewById(titleId);
            if (title != null) {
                ((TextView) title).setTextColor(color);
            }

            // Title divider
            final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
            final View titleDivider = getDialog().findViewById(titleDividerId);
            if (titleDivider != null) {
                titleDivider.setBackgroundColor(color);
            }
        }
    }
	
}
