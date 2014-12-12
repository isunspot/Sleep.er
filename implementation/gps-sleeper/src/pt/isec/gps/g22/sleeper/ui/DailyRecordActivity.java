package pt.isec.gps.g22.sleeper.ui;

import java.util.Calendar;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.SleeperApp;
import pt.isec.gps.g22.sleeper.core.time.TimeUtils;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAOImpl;
import pt.isec.gps.g22.sleeper.ui.ProfileActivity.TimePickerFragment;
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
import android.util.Log;
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
	long day;
	DayRecord dayRecord;
	Boolean editMode;
	int sleepHour, sleepMin, wakeupHour, wakeupMin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily_record);
		
		hideActionBar();
		
		sleeperApp = (SleeperApp)getApplication();
		
		Intent intent = getIntent();		
		idDayRecord  = intent.getIntExtra("idDayRecord", -1);
		day = intent.getLongExtra("day", -1);
				
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
		DayRecord tempRecord;
		
		if(dayRecord==null)
			tempRecord= new DayRecord();
		else
			tempRecord = dayRecord;
				
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(day*1000);
               
		//Set calendar        
        cal.set(Calendar.HOUR_OF_DAY,sleepHour);
        cal.set(Calendar.MINUTE,sleepMin);
        cal.set(Calendar.SECOND, 0); 
        
        
		tempRecord.setSleepDate(cal.getTimeInMillis()/1000);
		tempRecord.setExhaustion((int) rbExhaustion.getRating());
				
		//Set calendar
		if((wakeupHour <= cal.get(Calendar.HOUR_OF_DAY))&&(wakeupMin <= cal.get(Calendar.MINUTE)))
        	wakeupHour+=24;
		
        cal.set(Calendar.HOUR_OF_DAY,wakeupHour);
        cal.set(Calendar.MINUTE,wakeupMin);
        cal.set(Calendar.SECOND, 0);
		
		tempRecord.setWakeupDate(cal.getTimeInMillis()/1000);
		tempRecord.setSleepQuality((int) rbQualitySleep.getRating());
		
		return tempRecord;
	}
	
	private void populateFields(int idDayRecord){
		if(idDayRecord != -1)
		{			
			dayRecord = sleeperApp.getDayRecordDAO().loadDayRecord(idDayRecord);
			if(dayRecord!=null)
			{
				Calendar calendar = Calendar.getInstance();
			    calendar.setTimeInMillis(dayRecord.getSleepDate()*1000);
			    String sleepHour = (String) android.text.format.DateFormat.format("hh:mm", calendar.getTime());
			    tvSleepHourValue.setText(sleepHour);
			    
			    calendar.setTimeInMillis(dayRecord.getWakeupDate()*1000);
			    String wakeupHour = (String) android.text.format.DateFormat.format("hh:mm", calendar.getTime());
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
			    	sleepHour = hourOfDay;
			    	sleepMin = minute;
    			} else {
    				if(type == 2) {
    			    	String sWakeupHour = (String) android.text.format.DateFormat.format("hh:mm", cal.getTime());
    			    	tvWakeupHourValue.setText(sWakeupHour);
    			    	wakeupHour = hourOfDay;
    			    	wakeupMin = minute;
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
