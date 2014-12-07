package pt.isec.gps.g22.sleeper.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.dal.ProfileDAOImpl;
import pt.isec.gps.g22.sleeper.ui.R;

public class ProfileActivity extends Activity {
    
    ProfileDAOImpl profileDAOImpl;
    Profile profile;
    TimePicker tpFirstHourOfTheDay;
    DatePicker dpDateOfBirth;
    RadioButton rbFemale, rbMale;
    Button btOk, btCancel;
    int gender = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        dpDateOfBirth = (DatePicker) findViewById(R.id.dpBirthDay);
        tpFirstHourOfTheDay = (TimePicker) findViewById(R.id.tpFirstHourOfTheDay);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        btOk = (Button) findViewById(R.id.btOk);
        btCancel = (Button) findViewById(R.id.btCancel);

        boolean isDefined = getIntent().getBooleanExtra("isDefined",false);
        profileDAOImpl = new ProfileDAOImpl(ProfileActivity.this);
        
        if(isDefined) {    
            profile = profileDAOImpl.loadProfile();
            Calendar c = unixtimeToCalendar(profile.getDateOfBirth());
            dpDateOfBirth.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
            //tpFirstHourOfTheDay.setCurrentHour(currentHour);
            //tpFirstHourOfTheDay.setCurrentMinute(currentMinute);
            if(profile.getGender() == 0) {
                rbMale.setEnabled(true);
                rbFemale.setEnabled(false);
            } else {
                rbMale.setEnabled(false);
                rbFemale.setEnabled(true);
            }
        }
                
        rbMale.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                rbMale.setEnabled(true);
                rbFemale.setEnabled(false);
                gender = 0;
            }
        });
        
        rbFemale.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                rbFemale.setEnabled(true);
                rbMale.setEnabled(false);
                gender = 1;
            }
        });
        
        btOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profile == null) {
                    profile = new Profile();
                    profile.setGender(gender);
                    profile.setDateOfBirth((int)dateOfBirthToUnixtime());
                    profile.setFirstHourOfTheDay(182381293);
                    profileDAOImpl.insertProfile(profile);
                } else {
                    profileDAOImpl.updateProfile(profile);
                }
                finish();
            }
        });
        
        btCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private long dateOfBirthToUnixtime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, dpDateOfBirth.getYear());
        calendar.set(Calendar.MONTH, dpDateOfBirth.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, dpDateOfBirth.getDayOfMonth());
        return calendar.getTimeInMillis();
    }
    
    private Calendar unixtimeToCalendar(int unixtime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixtime);
        return calendar;
    }    
}