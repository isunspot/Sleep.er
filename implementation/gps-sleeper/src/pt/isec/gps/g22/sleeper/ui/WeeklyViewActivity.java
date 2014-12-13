package pt.isec.gps.g22.sleeper.ui;

import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.averageExhaustionLevel;
import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.averageSleepQuality;
import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.getChartDays;
import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.getMaxTimeSleptInADay;
import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.getMinTimeSleptInADay;
import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.getWeek;
import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.getWeekSleepDebt;
import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.getWeekStart;
import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.recordsToSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.ExhaustionLevel;
import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.core.SleepQuality;
import pt.isec.gps.g22.sleeper.core.SleeperApp;
import pt.isec.gps.g22.sleeper.core.time.TimeUtils;
import pt.isec.gps.g22.sleeper.core.time.TimeDelta;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAO;
import pt.isec.gps.g22.sleeper.dal.ProfileDAO;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

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

	private static final String[] DOW = { "S", "M", "T", "W", "T", "F", "S" };
	
	private DayRecordDAO dayRecordDAO;
	private ProfileDAO profileDAO;
	private XYPlot plot;
	
	private float x1,x2;
	static final int MIN_DISTANCE = 150;
	
	private long weekStart;
	private List<List<SeriesValue>> dayValuesList;
	private long weekSleepDebt;
	private TimeDelta minTimeSleptInADay;
	private TimeDelta maxTimeSleptInADay;
	private ExhaustionLevel averageExhaustionLevel;
	private SleepQuality averageSleepQuality;

	private TextView txtViewMaxHours;
	private TextView txtViewMinHours;
	private TextView txtViewSleepDebt;
	private TextView txtViewAvgExhaustion;
	private TextView txtViewAvgSleepQuality;
	
	MyBarFormatter wakeFormatter;
	MyBarFormatter sleepFormatter;
	MyBarFormatter undersleepFormatter;
	MyBarFormatter oversleepFormatter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_weekly_view);

		/*
		 * Get the DAOs 
		 */
		final SleeperApp app = (SleeperApp) getApplication();
		dayRecordDAO = app.getDayRecordDAO();
		profileDAO = app.getProfileDAO();
		
		/*
		 * Instantiate the formatters
		 */
		wakeFormatter = new MyBarFormatter(Color.argb(255, 143, 255, 183), Color.LTGRAY);
		sleepFormatter = new MyBarFormatter(Color.argb(255, 143, 227, 255), Color.LTGRAY);
		undersleepFormatter = new MyBarFormatter(Color.argb(255, 255, 114, 0), Color.LTGRAY);
		oversleepFormatter = new MyBarFormatter(Color.argb(255, 255, 210, 0), Color.LTGRAY);
		
		txtViewMaxHours = (TextView) findViewById(R.id.txtViewMaxHours);
		txtViewMinHours = (TextView) findViewById(R.id.txtViewMinHours);
		txtViewSleepDebt = (TextView) findViewById(R.id.txtViewSleepDebt);
		txtViewAvgExhaustion = (TextView) findViewById(R.id.txtViewAvgExhaustion);
		txtViewAvgSleepQuality = (TextView) findViewById(R.id.txtViewAvgSleepQuality);

		plot = (XYPlot) findViewById(R.id.barPlot);
		plot.setTitle("");
		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 3 * 60);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plot.setRangeBoundaries(0, 24 * 60, BoundaryMode.FIXED);
        plot.setDomainBoundaries(-0.5, 6.5, BoundaryMode.FIXED);
        plot.getLayoutManager().remove(plot.getLegendWidget());
        plot.getLayoutManager().remove(plot.getDomainLabelWidget());
        plot.getLayoutManager().remove(plot.getRangeLabelWidget());

        /*
         * Define the y-axis value formatter
         */
        plot.setRangeValueFormat(new Format() {

			@Override
			public StringBuffer format(Object object, StringBuffer buffer,
					FieldPosition field) {
				int delta = 0; //2 * 60;
				
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
				return null;
			}
			
		});
        
        /*
         * Define the x-axis value formatter
         */
        plot.setDomainValueFormat(new Format() {

			@Override
			public StringBuffer format(Object object, StringBuffer buffer,
					FieldPosition field) {
				
				buffer.append(DOW[((Double) object).intValue()] + "       ");
				
				return buffer;
			}

			@Override
			public Object parseObject(String string, ParsePosition position) {
				return null;
			}
        	
        });
        
//        plot.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				final float value = event.getAxisValue(MotionEvent.AXIS_X);
//				try {
//					final int x = new Double(plot.getGraphWidget().getXVal(value)).intValue();
//					// TODO Call daily view activity
//					
//					long day = weekStart + x * WeeklyViewUtils.DAY_SECONDS;
//					Intent intent = new Intent(WeeklyViewActivity.this, DayView.class);
//					intent.putExtra("day", day);
//            		startActivity(intent);
//				} catch (final IllegalArgumentException ex) {
//				}				
//			}
//		});
        
        plot.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: 
					return true;
				case MotionEvent.ACTION_UP:
					final float value = event.getAxisValue(MotionEvent.AXIS_X);
					try {
						final int x = new Double(plot.getGraphWidget().getXVal(value)).intValue();
						
						long day = weekStart + x * WeeklyViewUtils.DAY_SECONDS;
						Calendar cal = Calendar.getInstance();
						cal.setTimeInMillis(day);
												
						Intent intent = new Intent(WeeklyViewActivity.this, DayView.class);
						intent.putExtra("day", day);
	            		startActivity(intent);
						
						return true;	
					} catch (final IllegalArgumentException ex) {
						return false;
					}
					default: return false;
				}
			}
        	
        });
		
		final long now = new Date().getTime() / 1000;
		plot.setTitle(getWeekStr(getWeekStart(now,WeeklyViewUtils.DAY_SECONDS)));
		loadValues(now);
		bindValues();
	}
	
	private String getWeekStr(long weekStart){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(weekStart*1000);
		String str = cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR);
		cal = Calendar.getInstance();
		cal.setTimeInMillis((weekStart + WeeklyViewUtils.WEEK_SECONDS) * 1000);
		str += " to "+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR);
		return str;		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			x1 = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			x2 = event.getX();
			float deltaX = x2 - x1;

			if (Math.abs(deltaX) > MIN_DISTANCE) {
				// Left to Right swipe action
				if (x2 > x1) {
					weekStart -= WeeklyViewUtils.WEEK_SECONDS;
					loadValues(weekStart);
					bindValues();
					plot.setTitle(getWeekStr(weekStart));
					plot.redraw();					
				}

				// Right to left swipe action
				else {
					weekStart += WeeklyViewUtils.WEEK_SECONDS;
					loadValues(weekStart);
					bindValues();
					plot.setTitle(getWeekStr(weekStart));
					plot.redraw();					
				}

			} else {
				// consider as something else - a screen tap for example
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	private void loadValues(final long now) {
		final Profile profile = profileDAO.loadProfile();
		weekStart = getWeekStart(now, profile.getFirstHourOfTheDay() * WeeklyViewUtils.MINUTE_SECONDS);
		final List<WeekDay> week = getWeek(weekStart);
		final List<DayRecord> dayRecords = dayRecordDAO.getRecords(weekStart, weekStart + WeeklyViewUtils.WEEK_SECONDS);
		final ChartDay[] chartDays = getChartDays(profile, dayRecords, week, now);
		
		// Bar chart
		dayValuesList = recordsToSeries(chartDays);
		// Dashboard
		weekSleepDebt = getWeekSleepDebt(chartDays);
		minTimeSleptInADay = getMinTimeSleptInADay(chartDays);
		maxTimeSleptInADay = getMaxTimeSleptInADay(chartDays);
		averageExhaustionLevel = averageExhaustionLevel(chartDays);
		averageSleepQuality = averageSleepQuality(chartDays);
	}
	
	private void bindValues() {
		txtViewMaxHours.setText(TimeUtils.formatDuration((int) maxTimeSleptInADay));
		txtViewMinHours.setText(TimeUtils.formatDuration((int) minTimeSleptInADay));
		txtViewSleepDebt.setText(TimeUtils.formatDuration((int) weekSleepDebt));
		txtViewAvgExhaustion.setText(averageExhaustionLevel == null ? "0" : Integer.toString(averageExhaustionLevel.getLevel()));
		txtViewAvgSleepQuality.setText(averageSleepQuality == null ? "0" : Integer.toString(averageSleepQuality.getLevel()));
		
		// clear any previous series
		plot.clear();
		
		// iterate through the days
		for (int i = 0; i < dayValuesList.size(); i++) {
			final List<SeriesValue> dayValues = dayValuesList.get(i);
		
			// iterate through the values in the day
			for (int j = 0; j < dayValues.size(); j++) { 
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
	
	/**
	 * Returns the correct formatter according for the series type
	 * @param type the series type
	 * @return the series formatter
	 */
	private XYSeriesFormatter<?> getFormatter(final SeriesType type) {
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

/**
 * Custom bar formatter
 */
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

/**
 * Custom bar renderer
 */
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
