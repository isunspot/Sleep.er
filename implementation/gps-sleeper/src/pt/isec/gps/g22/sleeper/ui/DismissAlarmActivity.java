package pt.isec.gps.g22.sleeper.ui;

import java.io.IOException;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAOImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;

public class DismissAlarmActivity extends Activity {

	private MediaPlayer mMediaPlayer;
	private DayRecordDAOImpl dayRecordImp;
	private DayRecord dayRecord;
	private int idDayRecord;
    RatingBar rbQuality;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dismiss_alarm);
		Intent intent = getIntent();		
		idDayRecord  = intent.getIntExtra("idDayRecord", -1);
		
		dayRecordImp = new DayRecordDAOImpl(getApplicationContext());
		dayRecord = dayRecordImp.loadDayRecord(idDayRecord);
		final RatingBar rbQuality = (RatingBar) findViewById(R.id.rbQualitySleep);
		
		Button btSave = (Button) findViewById(R.id.btQualifySleep);
		btSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mMediaPlayer.stop();
				dayRecord.setSleepQuality((int) rbQuality.getRating());
				dayRecordImp.updateRecord(dayRecord);
								
				finish();
			}
		});
		
		Button btDismissAlarm = (Button) findViewById(R.id.btDismissAlarm);
		btDismissAlarm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mMediaPlayer.stop();
			}
		});
 
        playSound(this, getAlarmUri());
	}
	
	
	private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            Log.e("MediPlayer", e.getMessage());
        }
    }
 
    //Get an alarm sound. Try for an alarm. If none set, try notification, otherwise ringtone
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }
}
