package pt.isec.gps.g22.sleeper.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import pt.isec.gps.g22.sleeper.dal.DayRecordDAO;
import pt.isec.gps.g22.sleeper.dal.ProfileDAO;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.androidplot.ui.SeriesRenderer;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.FillDirection;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;

public class WeeklyViewActivity extends Activity {

	private DayRecordDAO dayRecordDAO;
	private ProfileDAO profileDAO;
	private XYPlot plot;

	MyBarFormatter wakeFormatter;
	MyBarFormatter sleepFormatter;
	MyBarFormatter undersleepFormatter;
	MyBarFormatter oversleepFormatter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("*************", "************");
		
		setContentView(R.layout.activity_weekly_view);

		wakeFormatter = new MyBarFormatter(Color.argb(200, 100, 150, 100), Color.LTGRAY);
		sleepFormatter = new MyBarFormatter(Color.argb(200, 150, 100, 100), Color.LTGRAY);
		undersleepFormatter = new MyBarFormatter(Color.argb(200, 100, 100, 150), Color.LTGRAY);
		oversleepFormatter = new MyBarFormatter(Color.argb(200, 150, 150, 150), Color.LTGRAY);
		
		// final int weekStart = 0; // TODO implement
		// final List<DayRecord> records =
		// dayRecordDAO.getWeekRecords(weekStart);
		// final Profile profile = profileDAO.getProfile();
		// final List<WeekDay> weekDays = WeeklyViewUtils.getWeek(weekStart);

		// final List<List<SeriesValue>> dayValuesList =
		// WeeklyViewUtils.recordsToSeries(profile, records, weekDays);
		final List<List<SeriesValue>> dayValuesList = new ArrayList<List<SeriesValue>>();
		final Random rand = new Random();
		final SeriesType[] types = new SeriesType[] { SeriesType.OVERSLEEP, SeriesType.UNDERSLEEP,  SeriesType.SLEEP,  SeriesType.WAKE }; 
		for (int i = 0; i < 7; i++) {
			dayValuesList.add(new ArrayList<SeriesValue>() {
				{
					add(new SeriesValue(1440, types[rand.nextInt(4)]));
					int numSeries = rand.nextInt(10);
					for (int j = 0; j < numSeries; j++) {
						add(new SeriesValue(rand.nextInt(1440), types[rand.nextInt(4)]));
					}
				}
			});	
		}
		

		plot = (XYPlot) findViewById(R.id.barPlot);

		for (int i = 0; i < dayValuesList.size(); i++) { // iterate through the
															// days
			final List<SeriesValue> dayValues = dayValuesList.get(i);

			for (int j = 0; j < dayValues.size(); j++) { // iterate through the
															// values in the day
				final SeriesValue value = dayValues.get(j);

				final Number[] series = new Number[dayValuesList.size()];
				for (int k = 0; k < dayValuesList.size(); k++) {
					if (k != i) {
						series[k] = 0;
					} else {
						series[k] = value.value;
					}
				}

				final XYSeries xySeries = new SimpleXYSeries(
						Arrays.asList(series),
						SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "series");
				plot.addSeries(xySeries, getFormatter(value.type));
			}
		}

	}

	private XYSeriesFormatter getFormatter(final SeriesType type) {
		if (type == SeriesType.WAKE) {
			return wakeFormatter;
		} else if (type == SeriesType.SLEEP) {
			return sleepFormatter;
		} else if (type == SeriesType.UNDERSLEEP) {
			return undersleepFormatter;
		} else {
			return oversleepFormatter;
		}
	}
}

class MyBarFormatter extends BarFormatter {
	public MyBarFormatter(int fillColor, int borderColor) {
		super(fillColor, borderColor);

		setFillDirection(FillDirection.LEFT);
	}

	@Override
	public Class<? extends SeriesRenderer> getRendererClass() {
		return MyBarRenderer.class;
	}

	@Override
	public SeriesRenderer getRendererInstance(XYPlot plot) {
		return new MyBarRenderer(plot);
	}
}

class MyBarRenderer extends BarRenderer<MyBarFormatter> {

	public MyBarRenderer(XYPlot plot) {
		super(plot);

		setBarWidthStyle(BarWidthStyle.VARIABLE_WIDTH);
		setBarGap(0);
	}

	/**
	 * Implementing this method to allow us to inject our special selection
	 * formatter.
	 * 
	 * @param index
	 *            index of the point being rendered.
	 * @param series
	 *            XYSeries to which the point being rendered belongs.
	 * @return
	 */
	@Override
	public MyBarFormatter getFormatter(int index, XYSeries series) {
		return getFormatter(series);
	}
}
