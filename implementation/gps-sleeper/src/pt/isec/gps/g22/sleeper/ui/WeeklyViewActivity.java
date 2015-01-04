package pt.isec.gps.g22.sleeper.ui;

import static java.lang.Math.abs;
import static pt.isec.gps.g22.sleeper.core.time.TimeUtils.formatDate;
import static pt.isec.gps.g22.sleeper.core.time.TimeUtils.formatDuration;
import static pt.isec.gps.g22.sleeper.core.time.TimeUtils.weeks;
import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.DAY;
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
import java.util.List;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.ExhaustionLevel;
import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.core.SleepQuality;
import pt.isec.gps.g22.sleeper.core.SleeperApp;
import pt.isec.gps.g22.sleeper.core.time.DateTime;
import pt.isec.gps.g22.sleeper.core.time.TimeDelta;
import pt.isec.gps.g22.sleeper.core.time.TimeOfDay;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAO;
import pt.isec.gps.g22.sleeper.dal.ProfileDAO;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

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

	private static final String LOG = "WeeklyView";	
	private static final String[] DOW = { "S", "M", "T", "W", "T", "F", "S" };
	static final int MIN_DISTANCE = 150;
	
	private DayRecordDAO dayRecordDAO;
	private ProfileDAO profileDAO;
	private XYPlot plot;
	
	private float x1,x2;
	
	private DateTime weekStart;
	private TimeOfDay dayStart;
	private List<List<SeriesValue>> dayValuesList;
	private TimeDelta weekSleepDebt;
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
		
		getActionBar().hide();

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
				final boolean isStartOfDay = dayStart.toSeconds() == 0;
				final TimeDelta delta = isStartOfDay ? TimeDelta.fromSeconds(0) : dayStart.asTimeDelta().subtract(DAY);
				final int deltaMinutes = (int) (delta.asSeconds() / 60);
				
				int value = (int) (24 * 60 + deltaMinutes - ((Double) object));
				if (value < 0) {
					value = 1440 + value;
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
        
        plot.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: 
					return true;
				case MotionEvent.ACTION_UP:
					final float value = event.getAxisValue(MotionEvent.AXIS_X);
					try {
						final float compensatedValue = value + 25; // <-- magic bullshit
						final int x = Double.valueOf(plot.getGraphWidget().getXVal(compensatedValue)).intValue();
						
						final TimeDelta dayStartDelta = dayStart.toSeconds() == 0 
								? TimeDelta.fromSeconds(0)
								: WeeklyViewUtils.DAY.subtract(dayStart.asTimeDelta());
						final DateTime day = weekStart.add(TimeDelta.duration(x * 24))
								.add(dayStartDelta); // compensate for the day start
												
						Intent intent = new Intent(WeeklyViewActivity.this, DayView.class);
						intent.putExtra("day", day.toUnixTimestamp());
	            		startActivity(intent);
						
						return true;	
					} catch (final IllegalArgumentException ex) {
						return false;
					}
					default: return false;
				}
			}
        	
        });
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		final int weekOffset = ((SleeperApp) getApplication()).getWeekOffset();
		Log.d(LOG, "Loading weekly view with a week offset of " + weekOffset);
		final DateTime now = DateTime.now().add(weeks(abs(weekOffset), weekOffset >= 0));
		loadValues(now);
		bindValues();
	}
	
	private String getChartTitle(final DateTime weekStart) {
		final TimeDelta dayStartDelta = dayStart.toSeconds() == 0
				? TimeDelta.fromSeconds(0)
				: WeeklyViewUtils.DAY.subtract(dayStart.asTimeDelta());
		final DateTime correctedWeekStart = weekStart.add(dayStartDelta); // compensate for the day start
		
		final TimeDelta weekDelta = TimeDelta.fromSeconds(weeks(1).asSeconds() - 1); // one second less to be the last second of the last day
		return formatDate(correctedWeekStart) + " to " + formatDate(correctedWeekStart.add(weekDelta));
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
				final SleeperApp app = ((SleeperApp) getApplication()); 
				if (x2 > x1) {
					final int weekOffset = app.decrementWeekOffset();
					Log.d(LOG, "Loading weekly view with a week offset of " + weekOffset);
					final DateTime now = DateTime.now().add(weeks(abs(weekOffset), weekOffset >= 0));
					//weekStart = getWeekStart(now, dayStart);
					loadValues(now);
					bindValues();
				}

				// Right to left swipe action
				else {
					final int weekOffset = app.incrementWeekOffset();
					Log.d(LOG, "Loading weekly view with a week offset of " + weekOffset);
					final DateTime now = DateTime.now().add(weeks(abs(weekOffset), weekOffset >= 0));
					//weekStart = getWeekStart(now, dayStart);
					loadValues(now);
					bindValues();
				}

			} else {
				// consider as something else - a screen tap for example
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	private void loadValues(final DateTime now) {
		final Profile profile = profileDAO.loadProfile();
		dayStart = TimeOfDay.fromMinutes(profile.getFirstHourOfTheDay());
		weekStart = getWeekStart(now, dayStart);
		plot.setTitle(getChartTitle(weekStart));
		final List<WeekDay> week = getWeek(weekStart);
		final List<DayRecord> dayRecords = dayRecordDAO.getRecords(weekStart.toUnixTimestamp(), weekStart.add(weeks(1)).toUnixTimestamp());
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
		txtViewMaxHours.setText(formatDuration(maxTimeSleptInADay));
		txtViewMinHours.setText(formatDuration(minTimeSleptInADay));
		txtViewSleepDebt.setText(formatDuration(weekSleepDebt));
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
		
		plot.redraw();
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
