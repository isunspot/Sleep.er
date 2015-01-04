package pt.isec.gps.g22.sleeper.ui;

import static pt.isec.gps.g22.sleeper.core.time.TimeUtils.years;

import java.util.Calendar;

import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.core.SleeperApp;
import pt.isec.gps.g22.sleeper.core.time.DateTime;
import pt.isec.gps.g22.sleeper.core.time.TimeOfDay;
import pt.isec.gps.g22.sleeper.core.time.TimeUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

public class ProfileActivity extends Activity {
    
	SleeperApp sleeper;
	Profile profile;
	
    TextView tvDateOfBirth,tvDateOfBirthValue,tvGender,tvGenderValue,tvFirstHour,tvFirstHourValue,tvSave;
    LinearLayout layoutDateOfBirth, layoutGender, layoutFirstHour, layoutSave;
    Typeface tf,tfBold;
    
	DateTime dateOfBirth;
    int gender = -1;
    CharSequence genders[] = {"Male","Female"};
    long firstHourOfTheDay = -1;
    
    private Context ctx;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        ctx = this;
        
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
        	dateOfBirth = DateTime.fromSeconds(profile.getDateOfBirth());
        	gender = profile.getGender();
        	firstHourOfTheDay = profile.getFirstHourOfTheDay();
        	
            tvDateOfBirthValue.setText(TimeUtils.formatDate(dateOfBirth));
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
            	if(dateOfBirth!=null&&gender!=-1&&firstHourOfTheDay!=-1){
	        	    profile.setDateOfBirth(dateOfBirth.toUnixTimestamp());
	        	    profile.setGender(gender);
	        	    profile.setFirstHourOfTheDay((int) firstHourOfTheDay);
	        	    
	        	    if (profile!=null){
	        	    	if(sleeper.profileDefined()) {
	        	    		sleeper.getProfileDAO().updateProfile(profile);
	        	    	} else {
	        	    		sleeper.getProfileDAO().insertProfile(profile);
	        	    		sleeper.defineProfile();
	        	    	}
	        	    }
	        	    
	        	    startActivity(new Intent(ctx, MainScreenActivity.class));
            	}else{
            		Toast.makeText(getApplicationContext(), "Please define all fields", Toast.LENGTH_SHORT).show();
            	}
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
			final TimeOfDay dayStart = sleeper.profileDefined() 
					? TimeOfDay.fromMinutes(profile.getFirstHourOfTheDay())
					: DateTime.now().toTimeOfDay();

    		return new TimePickerDialog(getActivity(), this, dayStart.getHours(), dayStart.getMinutes(), DateFormat.is24HourFormat(getActivity()));
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
    	DateTime max;
    	DateTime min;
    	
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
    	
    	public void onDateSet(final DatePicker view, final int year, final int month, final int day) {
    		if(view.isShown()) {
    			dateOfBirth = DateTime.fromDate(year, month + 1, day); // month is 0-based
    			String s = TimeUtils.formatDate(dateOfBirth);
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
                
        private DatePickerDialog setDatePicker() {
    		final DateTime now = DateTime.now();
    		
        	return new DatePickerDialog(getActivity(), this, now.getYear(), now.getMonth(), now.getDay());
        }
        
        private void setMax(DatePickerDialog datePickerDialog) {
        	max = DateTime.now().add(years(13, false));
    		
        	datePickerDialog.getDatePicker().setMaxDate(max.toMillis());
        }
        
        private void setMin(DatePickerDialog datePickerDialog) {
        	min = DateTime.now().add(years(120, false));
        	
    		datePickerDialog.getDatePicker().setMinDate(min.toMillis());
        }
        
        private void updateDate(DatePickerDialog datePickerDialog) {
        	final DateTime dob = DateTime.fromSeconds(profile.getDateOfBirth());
        	
        	datePickerDialog.updateDate(dob.getYear(), dob.getMonth(), dob.getDay());
        }
    }
    
}