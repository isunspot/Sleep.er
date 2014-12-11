package pt.isec.gps.g22.sleeper.ui;

import java.util.Calendar;
import java.util.List;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.SleeperApp;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import pt.isec.gps.g22.sleeper.core.TimeUtils;

public class CustomAdapter extends BaseAdapter {
	
	Context context;
	SleeperApp sleeper;
	List<DayRecord> listRecords;
	
	public CustomAdapter(Context context,SleeperApp sleeper,List<DayRecord> listRecords) {
		this.context = context;
		this.sleeper = sleeper;
		this.listRecords = listRecords;
	}
	
	@Override
	public int getCount() {
		return listRecords.size();
	}

	@Override
	public Object getItem(int i) {
		return listRecords.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int index, View view, final ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.list_item, parent, false);
		}
        final DayRecord dayRecord = listRecords.get(index);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dayRecord.getSleepDate()*1000);
        String str = (String) android.text.format.DateFormat.format("hh:mm", cal.getTime());
        
        TextView tvStartHour = (TextView) view.findViewById(R.id.tvStartHour);
        tvStartHour.setText(str);
  
        cal.setTimeInMillis(dayRecord.getWakeupDate()*1000);
        str = (String) android.text.format.DateFormat.format("hh:mm", cal.getTime());
        
        TextView tvEndHour = (TextView) view.findViewById(R.id.tvEndHour);
        tvEndHour.setText(str);
  
        LinearLayout editLayout = (LinearLayout) view.findViewById(R.id.LayoutEdit);
        LinearLayout deleteLayout = (LinearLayout) view.findViewById(R.id.LayoutDelete);
        
        editLayout.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		Intent intent = new Intent(context,DailyRecordActivity.class);
        		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		intent.putExtra("idDayRecord", dayRecord.getId());
        		context.startActivity(intent);
        	}
        });
        
        deleteLayout.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		sleeper.getDayRecordDAO().deleteRecord(dayRecord);
        		listRecords.remove(dayRecord);
        		refresAdapter(listRecords);
        	}
        });
 
        return view;
	}
	
	public synchronized void refresAdapter(List<DayRecord> dataRecords) {   
		listRecords.clear();
		listRecords.addAll(dataRecords);
	    notifyDataSetChanged();
	   }
}
