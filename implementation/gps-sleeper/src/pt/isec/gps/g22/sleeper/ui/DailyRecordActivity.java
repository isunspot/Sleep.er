package pt.isec.gps.g22.sleeper.ui;

import static pt.isec.gps.g22.sleeper.core.time.TimeUtils.formatHoursMinutes;

import java.util.Calendar;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.SleeperApp;
import pt.isec.gps.g22.sleeper.core.time.DateTime;
import pt.isec.gps.g22.sleeper.core.time.TimeDelta;
import pt.isec.gps.g22.sleeper.core.time.TimeOfDay;
import pt.isec.gps.g22.sleeper.core.time.TimeUtils;
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
	    	    DialogFragment newFragment = new TimePickerFragment(FragmentType.SLEEP);
	    	    newFragment.show(getFragmentManager(), "timePickerSH");
	        }
	    });
	    
	    wakeupHourLayout.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	    	    DialogFragment newFragment = new TimePickerFragment(FragmentType.WAKEUP);
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
		final DayRecord record = dayRecord == null ? new DayRecord() : dayRecord;
		
        final DateTime sleepTime = DateTime.fromDateTime(
        		day.getYear(),
        		day.getMonth(),
        		day.getDay(),
        		sleep.getHours(), 
        		sleep.getMinutes(), 0);
        
		record.setSleepDate(sleepTime.toUnixTimestamp());
		record.setExhaustion((int) rbExhaustion.getRating());
				
		DateTime wakeupTime = DateTime.fromDateTime(
				day.getYear(),
        		day.getMonth(),
        		day.getDay(),
        		wakeup.getHours(), 
        		wakeup.getMinutes(), 0);
		
		if (wakeup.compareTo(sleep) <= 0) {
			wakeupTime = wakeupTime.add(TimeDelta.duration(24));
		}
			
		record.setWakeupDate(wakeupTime.toUnixTimestamp());
		record.setSleepQuality((int) rbQualitySleep.getRating());
		
		return record;
	}
	
	private void populateFields(int idDayRecord){
		if(idDayRecord != -1)
		{			
			dayRecord = sleeperApp.getDayRecordDAO().loadDayRecord(idDayRecord);
			if(dayRecord != null)
			{
				final DateTime sleepDate = DateTime.fromSeconds(dayRecord.getSleepDate());
				sleep = TimeOfDay.at(sleepDate.getHours(), sleepDate.getMinutes()); 
			    String sleepHour = (String) android.text.format.DateFormat.format("hh:mm", sleepDate.asCalendar().getTime());
			    tvSleepHourValue.setText(sleepHour);
			    
			    final DateTime wakeupDate = DateTime.fromSeconds(dayRecord.getWakeupDate());
			    wakeup = TimeOfDay.at(wakeupDate.getHours(), wakeupDate.getMinutes());
			    String wakeupHour = (String) android.text.format.DateFormat.format("hh:mm", wakeupDate.asCalendar().getTime());
			    tvWakeupHourValue.setText(wakeupHour);
			}
		}
	}
	
    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    	FragmentType type;
    	public TimePickerFragment(FragmentType type) {
    		this.type = type;
    	}
    	
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		TimeOfDay time;
    		    		
    		if(type == FragmentType.SLEEP) {
    			if(dayRecord != null) {
    			    time = DateTime.fromSeconds(dayRecord.getSleepDate()).toTimeOfDay();
    			} else {
    				time = TimeOfDay.at(23, 0);
    			}
    		} else {
    			if(dayRecord != null) {
    				time = DateTime.fromSeconds(dayRecord.getWakeupDate()).toTimeOfDay();
    			} else {
    				time = TimeOfDay.at(8, 0);
    			}
    		}
    		
    		return new TimePickerDialog(getActivity(), this, time.getHours(), time.getMinutes(), DateFormat.is24HourFormat(getActivity()));
    	}

    	@Override
    	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    		if(view.isShown()) {
				final TimeOfDay time = TimeOfDay.at(hourOfDay, minute);
				
    			if(type == FragmentType.SLEEP) {
			    	tvSleepHourValue.setText(formatHoursMinutes(time));
			    	sleep = time;
    			} else {
			    	tvWakeupHourValue.setText(formatHoursMinutes(time));
			    	wakeup = time;
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
    
	private enum FragmentType {
		SLEEP,
		WAKEUP
	}
	
}
