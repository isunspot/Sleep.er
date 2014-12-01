package pt.isec.gps.g22.sleeper.ui;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
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
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.FillDirection;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesFormatter;
import com.androidplot.xy.XYStepMode;

public class WeeklyViewActivity extends Activity {

	private static final String[] DOW = { "S", "M", "T", "W", "T", "F", "S", "" };
	
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

		wakeFormatter = new MyBarFormatter(Color.argb(255, 143, 255, 183), Color.LTGRAY);
		sleepFormatter = new MyBarFormatter(Color.argb(255, 143, 227, 255), Color.LTGRAY);
		undersleepFormatter = new MyBarFormatter(Color.argb(255, 255, 114, 0), Color.LTGRAY);
		oversleepFormatter = new MyBarFormatter(Color.argb(255, 255, 210, 0), Color.LTGRAY);
		
		// final int weekStart = 0; // TODO implement
		// final List<DayRecord> records =
		// dayRecordDAO.getWeekRecords(weekStart);
		// final Profile profile = profileDAO.getProfile();
		// final List<WeekDay> weekDays = WeeklyViewUtils.getWeek(weekStart);

		// final List<List<SeriesValue>> dayValuesList =
		// WeeklyViewUtils.recordsToSeries(profile, records, weekDays);

		//		final List<List<SeriesValue>> dayValuesList = new ArrayList<List<SeriesValue>>();
//		final Random rand = new Random();
//		final SeriesType[] types = new SeriesType[] { SeriesType.OVERSLEEP, SeriesType.UNDERSLEEP,  SeriesType.SLEEP,  SeriesType.WAKE }; 
//		for (int i = 0; i < 7; i++) {
//			dayValuesList.add(new ArrayList<SeriesValue>() {
//				{
//					add(new SeriesValue(1440, types[rand.nextInt(4)]));
//					int numSeries = rand.nextInt(10);
//					for (int j = 0; j < numSeries; j++) {
//						add(new SeriesValue(rand.nextInt(1440), types[rand.nextInt(4)]));
//					}
//				}
//			});	
//		}
		
		final List<List<SeriesValue>> dayValuesList = new ArrayList<List<SeriesValue>>() {{
				add(new ArrayList<SeriesValue>() {{
					add(new SeriesValue(1440, SeriesType.WAKE));
					add(new SeriesValue(1440 - 60, SeriesType.SLEEP));
					add(new SeriesValue(1440 - 8 * 60, SeriesType.WAKE));
					add(new SeriesValue(60, SeriesType.SLEEP));
				}});
				add(new ArrayList<SeriesValue>() {{
					add(new SeriesValue(1440, SeriesType.SLEEP));
					add(new SeriesValue(1440 - 6 * 60, SeriesType.UNDERSLEEP));
					add(new SeriesValue(1440 - 6 * 60 - 30, SeriesType.WAKE));
				}});
				add(new ArrayList<SeriesValue>() {{
					add(new SeriesValue(1440, SeriesType.WAKE));
					add(new SeriesValue(1440 - 30, SeriesType.SLEEP));
					add(new SeriesValue(1440 - 9 * 60 - 30, SeriesType.WAKE));
				}});
				add(new ArrayList<SeriesValue>() {{
					add(new SeriesValue(1440, SeriesType.WAKE));
					add(new SeriesValue(1440 - 60, SeriesType.SLEEP));
					add(new SeriesValue(1440 - 7 * 60 - 30, SeriesType.WAKE));	
				}});
				add(new ArrayList<SeriesValue>() {{
				}});
				add(new ArrayList<SeriesValue>() {{
				}});
				add(new ArrayList<SeriesValue>() {{
				}});
		}};

		plot = (XYPlot) findViewById(R.id.barPlot);
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 3 * 60);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plot.setRangeBoundaries(0, 24 * 60, BoundaryMode.FIXED);
        plot.setDomainBoundaries(-0.5, 6.5, BoundaryMode.FIXED);
        plot.getLayoutManager().remove(plot.getLegendWidget());
        plot.getLayoutManager().remove(plot.getDomainLabelWidget());
        plot.getLayoutManager().remove(plot.getRangeLabelWidget());

        // y-axis
        plot.setRangeValueFormat(new Format() {

			@Override
			public StringBuffer format(Object object, StringBuffer buffer,
					FieldPosition field) {
				int delta = 2 * 60;
				
				int value = (int) (24 * 60 - delta - ((Double) object));
				if (value < 0) {
					value = 24 * 60 + value;
				}
				
				if (value % 3 * 60 == 0) {
					buffer.append(value / 60);
				}
				
				return buffer;
			}

			@Override
			public Object parseObject(String string, ParsePosition position) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
        
        // x-axis
        plot.setDomainValueFormat(new Format() {

			@Override
			public StringBuffer format(Object object, StringBuffer buffer,
					FieldPosition field) {
				
				buffer.append("            " + DOW[((Double) object).intValue()]);
				
				return buffer;
			}

			@Override
			public Object parseObject(String string, ParsePosition position) {
				// TODO Auto-generated method stub
				return null;
			}
        	
        });
        
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
