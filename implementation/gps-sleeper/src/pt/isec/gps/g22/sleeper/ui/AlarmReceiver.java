package pt.isec.gps.g22.sleeper.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	 @Override
	 public void onReceive(Context context, Intent intent) {
	
		 final int idDayRecord = intent.getIntExtra("idDayRecord", -1);
		 Intent i = new Intent(context,DismissAlarmActivity.class);  
    
		 i.putExtra("idDayRecord", idDayRecord);
		 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 context.startActivity(i);          
	 }
}
