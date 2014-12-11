package pt.isec.gps.g22.sleeper.ui;

import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.core.SleeperApp;
import pt.isec.gps.g22.sleeper.core.TimeUtils;
import pt.isec.gps.g22.sleeper.ui.R;

public class ProfileActivity extends Activity {
    
	SleeperApp sleeper;
	Profile profile;
	
    TextView tvDateOfBirth,tvDateOfBirthValue,tvGender,tvGenderValue,tvFirstHour,tvFirstHourValue,tvSave;
    LinearLayout layoutDateOfBirth, layoutGender, layoutFirstHour, layoutSave;
    Typeface tf,tfBold;
    
	long dateOfBirth = 0;
    int gender = 0;
    CharSequence genders[] = {"Male","Female"};
    int firstHourOfTheDay = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        sleeper = (SleeperApp)getApplication();
    	profile = sleeper.getProfile();
        
        tvDateOfBirth = (TextView) findViewById(R.id.tvDateOfBirth);
        tvGender = (TextView) findViewById(R.id.tvGender);
        tvFirstHour = (TextView) findViewById(R.id.tvFirstHour);
        tvDateOfBirthValue = (TextView) findViewById(R.id.tvDateOfBirthValue);
        tvGenderValue = (TextView) findViewById(R.id.tvGenderValue);
        tvFirstHourValue = (TextView) findViewById(R.id.tvFirstHourValue);
        tvSave = (TextView) findViewById(R.id.tvSave);
        
        hideActionBar();
        setFonts();
        
        if(sleeper.profileDefined()) {
        	dateOfBirth = profile.getDateOfBirth()*1000;
        	gender = profile.getGender();
        	firstHourOfTheDay = profile.getFirstHourOfTheDay();
        	
            tvDateOfBirthValue.setText(TimeUtils.getDate(dateOfBirth));
            tvGenderValue.setText(genders[gender]);
            tvFirstHourValue.setText(TimeUtils.getTime(firstHourOfTheDay));
        }
        
        layoutDateOfBirth = (LinearLayout)findViewById(R.id.LayoutDateOfBirth);
        layoutGender = (LinearLayout)findViewById(R.id.LayoutGender);
        layoutFirstHour = (LinearLayout)findViewById(R.id.LayoutFirstHour);
        layoutSave = (LinearLayout)findViewById(R.id.LayoutSave);
        
        layoutDateOfBirth.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
        	    DialogFragment newFragment = new DatePickerFragment();
        	    newFragment.show(getFragmentManager(), "datePicker");
           }
        }); 
        
        layoutGender.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            		CustomDialogFragment newFragment = new CustomDialogFragment();
            	    newFragment.show(getFragmentManager(), "customDialogFragment");
            }
         }); 
        
        layoutFirstHour.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
        	    DialogFragment newFragment = new TimePickerFragment();
        	    newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        
        layoutSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
        	    profile.setDateOfBirth(dateOfBirth/1000);
        	    profile.setGender(gender);
        	    profile.setFirstHourOfTheDay(firstHourOfTheDay);
        	    
        	    if(sleeper.profileDefined()) {
        	    	sleeper.getProfileDAO().updateProfile(profile);
        	    } else {
        	    	sleeper.getProfileDAO().insertProfile(profile);
        	    	sleeper.defineProfile();
        	    }
        	    finish();
            }
        });
    }
        
    private void hideActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }
    
    private void setFonts() {
        tf = Typeface.createFromAsset(getAssets(), "NotoSans-Regular.ttf"); 
        tfBold = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf"); 
        
        tvDateOfBirth.setTypeface(tf);
        tvGender.setTypeface(tf);
        tvFirstHour.setTypeface(tf);
        tvDateOfBirthValue.setTypeface(tfBold);
        tvGenderValue.setTypeface(tfBold);
        tvFirstHourValue.setTypeface(tfBold);
        tvSave.setTypeface(tf);
    }
  
    public class CustomDialogFragment extends DialogFragment {
        public CustomDialogFragment() {
        }

    	
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setTitle(R.string.strGender)
    		.setItems(genders, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				if(which == 0) {
    					gender = 0;
    				} else {
    					if(which == 1) {
    						gender = 1;
    					}
    				}
    				tvGenderValue.setText(genders[gender]);
    			}
    		});		
    		return builder.create();
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
        	
    public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		final Calendar c = Calendar.getInstance();
    		int hour, minute;
    		
    		if(sleeper.profileDefined()) {
    			c.set(Calendar.HOUR_OF_DAY, TimeUtils.minutesToHours(profile.getFirstHourOfTheDay()));
    			c.set(Calendar.MINUTE, TimeUtils.minutesToMinutes(profile.getFirstHourOfTheDay()));
    		}
    		
			hour = c.get(Calendar.HOUR_OF_DAY);
			minute = c.get(Calendar.MINUTE);

    		return new TimePickerDialog(getActivity(), this, hour, minute,DateFormat.is24HourFormat(getActivity()));
    	}

    	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    		if(view.isShown()) {
    			firstHourOfTheDay = TimeUtils.convertToMinutes(hourOfDay,minute);
    			tvFirstHourValue.setText(TimeUtils.getTime(firstHourOfTheDay));
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
    
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    	Calendar max;
    	Calendar min;
    	
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		
    		DatePickerDialog datePickerDialog = setDatePicker();
    		setMin(datePickerDialog);
    		setMax(datePickerDialog);
    		if(sleeper.profileDefined()) {
    			updateDate(datePickerDialog);
    		}
    		return datePickerDialog;
    	}
    	
    	public void onDateSet(DatePicker view, int year, int month, int day) {
    		if(view.isShown()) {
    			dateOfBirth = dateToUnixtime(year,month,day);
    			String s = TimeUtils.getDate(dateOfBirth);
    			tvDateOfBirthValue.setText(s);
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
        
        private long dateToUnixtime(int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, day);
            return cal.getTimeInMillis();
        }
                
        private DatePickerDialog setDatePicker() {
    		Calendar cal = Calendar.getInstance();
        		
    		int year = cal.get(Calendar.YEAR);
    		int month = cal.get(Calendar.MONTH);
    		int day = cal.get(Calendar.DAY_OF_MONTH);
    		
        	return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        
        private void setMax(DatePickerDialog datePickerDialog) {
        	max = Calendar.getInstance();
    		max.add(Calendar.YEAR,-13);
    		datePickerDialog.getDatePicker().setMaxDate(max.getTimeInMillis());
        }
        
        private void setMin(DatePickerDialog datePickerDialog) {
        	min = Calendar.getInstance();
    		min.add(Calendar.YEAR,-120);
    		datePickerDialog.getDatePicker().setMinDate(min.getTimeInMillis());
        }
        
        private void updateDate(DatePickerDialog datePickerDialog) {
        	Calendar cal = Calendar.getInstance();
        	cal.setTimeInMillis(profile.getDateOfBirth());
        	int year = cal.get(Calendar.YEAR);
        	int month = cal.get(Calendar.MONTH);
        	int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        	datePickerDialog.updateDate(year,month,dayOfMonth);
        }
    }
    
}