package pt.isec.gps.g22.sleeper.ui;

import static pt.isec.gps.g22.sleeper.core.time.TimeDelta.duration;
import static pt.isec.gps.g22.sleeper.core.time.TimeDelta.fromSeconds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.ExhaustionLevel;
import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.core.SleepDelta;
import pt.isec.gps.g22.sleeper.core.SleepDuration;
import pt.isec.gps.g22.sleeper.core.SleepQuality;
import pt.isec.gps.g22.sleeper.core.time.DateTime;
import pt.isec.gps.g22.sleeper.core.time.TimeDelta;
import pt.isec.gps.g22.sleeper.core.time.TimeOfDay;
import pt.isec.gps.g22.sleeper.core.time.TimeUtils;

public class WeeklyViewUtils {
	
	static final TimeDelta MINUS_1_SECOND = TimeDelta.duration(0, 0, 1, false);
	static final TimeDelta MINUTE = TimeDelta.duration(0, 1);
	static final TimeDelta HOUR = TimeDelta.duration(1);
	static final TimeDelta DAY = TimeDelta.duration(24);
	static final TimeDelta WEEK = TimeDelta.duration(7 * 24);
	
	static DateTime getWeekStart(final DateTime now, final TimeOfDay dayStart) {
		final Calendar cal = now.asCalendar();
		final int days = (cal.get(Calendar.DAY_OF_WEEK) - 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return now
				.add(duration(days * 24, false))
				.add(dayStart.asTimeDelta().subtract(DAY));
	}
	
	/**
	 * Calculates a list of the series values for each chart day
	 * @param chartDays an array of chart days
	 * @return a list with lists of series values
	 */
	static List<List<SeriesValue>> recordsToSeries(final ChartDay[] chartDays) {
		final List<List<SeriesValue>> dayValuesList = new ArrayList<List<SeriesValue>>();
		for(int i = 0; i < chartDays.length; i++) {
			final ChartDay chartDay = chartDays[i];
			final List<SeriesValue> dayValues = new ArrayList<SeriesValue>();
			final int recordCount = chartDay.records.size();
			
			dayValues.add(new SeriesValue(24 * 60, SeriesType.WAKE));
			if (recordCount == 1) {
				dayValues.addAll(recordValues(chartDay.from, chartDay.until, chartDay.records.get(0), chartDay.optimumWakingTime));
			} else if (recordCount > 1) {
				final int lastRecordIndex = recordCount - 1;
				for (int j = 0; j < recordCount; j++) {
					final DayRecord dayRecord = chartDay.records.get(j);
					final boolean isLast = j == lastRecordIndex;
					
					if (isLast) {
						dayValues.addAll(recordValues(chartDay.from, chartDay.until, dayRecord, chartDay.optimumWakingTime));
					} else {
						final DateTime wakeup = DateTime.fromSeconds(dayRecord.getWakeupDate());
						dayValues.addAll(recordValues(chartDay.from, chartDay.until, dayRecord, wakeup));
					}
				}
			}
			
			dayValuesList.add(dayValues);
		}

		return dayValuesList; 
	}
	
	/**
	 * Calculates the chart days
	 * @param profile the user profile
	 * @param records a list of day records
	 * @param weekDays a list of week days
	 * @param now the current moment
	 * @return an array of chart days
	 */
	static ChartDay[] getChartDays(final Profile profile, final List<DayRecord> records, final List<WeekDay> weekDays, final DateTime now) {
		final ChartDay[] chartDays = new ChartDay[weekDays.size()];
		
		TimeDelta accumDebt = TimeDelta.fromSeconds(0);
		for(int i = 0; i < weekDays.size(); i++) {
			/*
			 * Determine which records belong to the day
			 */
			final WeekDay weekDay = weekDays.get(i); 
			List<DayRecord> dayRecords = new ArrayList<DayRecord>();
			
			for (DayRecord record : records) {
				final DateTime sleep = DateTime.fromSeconds(record.getSleepDate());
				final DateTime wakeup = DateTime.fromSeconds(record.getWakeupDate());
				
				final boolean recordInDay = 
					(sleep.afterOrSame(weekDay.from) && sleep.beforeOrSame(weekDay.until) ||
					(wakeup.afterOrSame(weekDay.from) && wakeup.beforeOrSame(weekDay.until)));

				if (recordInDay) { 
					dayRecords.add(record);
				}
			}
			
			final ExhaustionLevel exhaustionLevel = averageExhaustionLevel(dayRecords);
			final SleepQuality sleepQuality = averageSleepQuality(dayRecords);
			
			/*
			 * Calculate the optimum waking time and the sleep debt
			 */
			DateTime optimumWakingTime;
			TimeDelta debt = TimeDelta.fromSeconds(0);
			if (dayRecords.size() == 0) { 
				// didn't sleep
				optimumWakingTime = optimumWakingTime(weekDay.from, profile, accumDebt, exhaustionLevel, sleepQuality, now);
				debt = optimumWakingTime.diff(weekDay.from);
			} else if (dayRecords.size() == 1) { 
				// 1 sleep period
				final DateTime sleep = DateTime.fromSeconds(dayRecords.get(0).getSleepDate());
				final DateTime wakeup = DateTime.fromSeconds(dayRecords.get(0).getWakeupDate());
				
				optimumWakingTime = optimumWakingTime(sleep, profile, accumDebt, exhaustionLevel, sleepQuality, now);
				debt = optimumWakingTime.diff(wakeup);
			} else { 
				// multiple sleep periods
				final TimeDelta realSleep = sleepSum(records); // Time slept during the day
				final TimeDelta expectedSleep = optimumWakingTime(weekDay.from, profile, accumDebt, exhaustionLevel, sleepQuality, now).diff(weekDay.from);

				debt = expectedSleep.subtract(realSleep);
				optimumWakingTime = DateTime.fromSeconds(dayRecords.get(dayRecords.size() - 1).getSleepDate()).add(debt);
			}

			chartDays[i] = new ChartDay(weekDay.from, weekDay.until, debt, accumDebt, optimumWakingTime, dayRecords);
			
			accumDebt = accumDebt.add(debt);
		}
		
		// Instantiate the days that don't have 
		for (int i = 0; i < chartDays.length; i++) {
			if (i == 0) {
				accumDebt = TimeDelta.fromSeconds(0);
			} else {
				accumDebt = chartDays[i - 1].accumulatedDebt;
			}
			
			if (chartDays[i] == null) {
				final WeekDay weekDay = weekDays.get(i);
				chartDays[i] = new ChartDay(weekDay.from, weekDay.until, TimeDelta.fromSeconds(0), TimeDelta.fromSeconds(0), weekDay.from, new ArrayList<DayRecord>());
			}
		}
		
		return chartDays;
	}
	
	/**
	 * Calculates the week sleep debt
	 * @param chartDays the week days
	 * @return the week sleep debt
	 */
	static TimeDelta getWeekSleepDebt(final ChartDay[] chartDays) {
		return chartDays[chartDays.length - 1].accumulatedDebt;
	}
	
	/**
	 * Calculates the minimum time slept in a day
	 * @param chartDays an array of chart days
	 * @return the minimum time slept in a day
	 */
	static TimeDelta getMinTimeSleptInADay(final ChartDay[] chartDays) {
		if (chartDays.length < 1) {
			throw new IllegalArgumentException("Invalid chart days");
		}
		
		TimeDelta min = sleepSum(chartDays[0].records);
		
		for (int i = 1; i < chartDays.length; i++) {
			TimeDelta slept = sleepSum(chartDays[i].records);
			
			if (slept.compareTo(min) < 0) {
				min = slept;
			}
		}
		
		return min;
	}
	
	/**
	 * Calculates the maximum time slept in a day
	 * @param chartDays an array of chart days
	 * @return the maximum time slept in a day
	 */
	static TimeDelta getMaxTimeSleptInADay(final ChartDay[] chartDays) {
		if (chartDays.length < 1) {
			throw new IllegalArgumentException("Invalid chart days");
		}
		
		TimeDelta max = sleepSum(chartDays[0].records);
		
		for (int i = 1; i < chartDays.length; i++) {
			final TimeDelta slept = sleepSum(chartDays[i].records);
			
			if (slept.compareTo(max) > 0) {
				max = slept;
			}
		}
		
		return max;
	}
	
	/**
	 * Calculates the sum of the time slept over a list of day records.
	 * @param records the day records list
	 * @return the sum of the time slept in minutes
	 */
	static TimeDelta sleepSum(final List<DayRecord> records) {
		TimeDelta sleepSum = fromSeconds(0);
		for (DayRecord record : records) {
			sleepSum = sleepSum.add(fromSeconds(record.getWakeupDate() - record.getSleepDate()));
		}
		
		return sleepSum;
	}
	
	/**
	 * Calculates the average exhaustion level over a list of day records. Only the records with
	 * exhaustion level set (level > 0) are considered for the average calculation.
	 * @param records the day records list
	 * @return the calculated average or null if the records don't have exhaustion level set
	 */
	static ExhaustionLevel averageExhaustionLevel(final List<DayRecord> records) {
		int exaustion = 0;
		int count = 0;
		
		for(final DayRecord record : records) {
			if (record.getExhaustion() > 0) {
				count++;
				exaustion += record.getExhaustion();
			}
		}
		
		return count == 0 ? null : ExhaustionLevel.fromInt(exaustion / count);
	}
	
	/**
	 * Calculates the average exhaustion level over an array of chart days. Only the days with
	 * exhaustion level set (level > 0) are considered for the average calculation.
	 * @param chartDays the day records
	 * @return the calculated average or null if the days don't have exhaustion level set 
	 */
	static ExhaustionLevel averageExhaustionLevel(final ChartDay[] chartDays) {
		final ExhaustionLevel[] levels = new ExhaustionLevel[chartDays.length];
		
		for (int i = 0; i < chartDays.length; i++) {
			levels[i] = averageExhaustionLevel(chartDays[i].records);
		}
		
		int count = 0;
		int sum = 0;
		for (int i = 0; i < levels.length; i++) {
			final ExhaustionLevel level = levels[i];
			if (level != null) {
				count++;
				sum += level.getLevel();
			}
		}
		
		return count == 0 ? null : ExhaustionLevel.fromInt(sum / count);
	}
	
	/**
	 * Calculates the average sleep quality level over a list of day records. Only the records with
	 * sleep quality set (level > 0) are considered for the average calculation.
	 * @param records the day records list
	 * @return the calculated average or null if the records don't have sleep quality set.
	 */
	static SleepQuality averageSleepQuality(final List<DayRecord> records) {
		int quality = 0;
		int count = 0;
		
		for(final DayRecord record : records) {
			if (record.getSleepQuality() > 0) {
				count++;
				quality += record.getSleepQuality();
			}
		}
		
		return count == 0 ? null : SleepQuality.fromInt(quality / count);
	}
	
	/**
	 * Calculates the average sleep quality level over a list of chart days. Only the days with
	 * sleep quality set (level > 0) are considered for the average calculation.
	 * @param records the day records
	 * @return the calculated average or null if the days don't have sleep quality set.
	 */
	static SleepQuality averageSleepQuality(final ChartDay[] chartDays) {
		final SleepQuality[] levels = new SleepQuality[chartDays.length];
		
		for (int i = 0; i < chartDays.length; i++) {
			levels[i] = averageSleepQuality(chartDays[i].records);
		}
		
		int count = 0;
		int sum = 0;
		for (int i = 0; i < levels.length; i++) {
			final SleepQuality level = levels[i];
			if (level != null) {
				count++;
				sum += level.getLevel();
			}
		}
		
		return count == 0 ? null : SleepQuality.fromInt(sum / count);
	}
	
	/**
	 * Returns the list of series values that correspond to a day record
	 * @param record the day records
	 * @param optimumWakingTime the optimum waking time for the period int in the record
	 * @return the list of series values that matches the record
	 */
	static List<SeriesValue> recordValues(final DateTime dayStart, final DateTime dayEnd, final DayRecord record, final DateTime optimumWakingTime) {
		final List<SeriesValue> values = new ArrayList<SeriesValue>();
		final DateTime sleep = DateTime.fromSeconds(record.getSleepDate());
		final DateTime wakeup = DateTime.fromSeconds(record.getWakeupDate());
		
		if (sleep.before(dayStart)) {
			/*
			 * sleep period starts in previous day 
			 */
			if (optimumWakingTime.before(wakeup)) { // oversleep
				if (optimumWakingTime.after(dayStart)) {
					values.add(new SeriesValue(barValue(dayStart, dayStart), SeriesType.SLEEP));
					values.add(new SeriesValue(barValue(dayStart, optimumWakingTime), SeriesType.OVERSLEEP));	
				} else {
					values.add(new SeriesValue(barValue(dayStart, dayStart), SeriesType.OVERSLEEP));
				}
				values.add(new SeriesValue(barValue(dayStart, wakeup), SeriesType.WAKE));	
			} else if (optimumWakingTime.after(wakeup)) { // undersleep
				if (wakeup.after(dayStart)) {
					values.add(new SeriesValue(barValue(dayStart, dayStart), SeriesType.SLEEP));
					values.add(new SeriesValue(barValue(dayStart, wakeup), SeriesType.UNDERSLEEP));
				} else {
					values.add(new SeriesValue(barValue(dayStart, dayStart), SeriesType.UNDERSLEEP));
				}
				values.add(new SeriesValue(barValue(dayStart, optimumWakingTime), SeriesType.WAKE));
			} else { // exact sleep
				values.add(new SeriesValue(barValue(dayStart, dayStart), SeriesType.SLEEP));
				values.add(new SeriesValue(barValue(dayStart, wakeup), SeriesType.WAKE));
			}
		} else if (wakeup.after(dayEnd)) {
			/*
			 * sleep period ends in the next day
			 */
			values.add(new SeriesValue(barValue(dayStart, sleep), SeriesType.SLEEP));
			if (optimumWakingTime.before(wakeup)) { // oversleep
				if (optimumWakingTime.before(dayEnd)) {
					values.add(new SeriesValue(barValue(dayStart, optimumWakingTime), SeriesType.OVERSLEEP));
				}
			} else if (optimumWakingTime.after(wakeup)) { // undersleep
				if (wakeup.before(dayEnd)) {
					values.add(new SeriesValue(barValue(dayStart, wakeup), SeriesType.UNDERSLEEP));
				}
			}
		} else {
			/*
			 * sleep period starts and ends in the current day
			 */
			values.add(new SeriesValue(barValue(dayStart, sleep), SeriesType.SLEEP));
			if (optimumWakingTime.before(wakeup)) { // oversleep
				values.add(new SeriesValue(barValue(dayStart, optimumWakingTime), SeriesType.OVERSLEEP));
				values.add(new SeriesValue(barValue(dayStart, wakeup), SeriesType.WAKE));
			} else if (optimumWakingTime.after(wakeup)) { // undersleep
				values.add(new SeriesValue(barValue(dayStart, wakeup), SeriesType.UNDERSLEEP));
				values.add(new SeriesValue(barValue(dayStart, optimumWakingTime), SeriesType.WAKE));
			} else { // exact sleep
				values.add(new SeriesValue(barValue(dayStart, wakeup), SeriesType.WAKE));
			}
		}
		
		return values;
	}
	
	static long barValue(final DateTime dayStart, final DateTime value) {
		return (24 * 60) - (value.diff(dayStart)).asSeconds() / 60;
	}
	
	/**
	 * Calculates the optimum wake time for a sleep period
	 * @param start the start of the sleep period
	 * @param profile the user profile
	 * @param accumDebt the accumulated sleep debt 
	 * @param exhaustionLevel the exhaustion level at the start of the period
	 * @param sleepQuality the sleep quality of the previous period
	 * @return the optimum waking time
	 */
	static DateTime optimumWakingTime(final DateTime start, final Profile profile, final TimeDelta accumDebt, final ExhaustionLevel exhaustionLevel, 
			final SleepQuality sleepQuality, final DateTime now) {
		final boolean isMale = profile.getGender() == 0;
		final DateTime dateOfBirth = DateTime.fromSeconds(profile.getDateOfBirth());
		final TimeDelta age = TimeUtils.ageFromDateOfBirth(dateOfBirth, now);
		final TimeDelta base = SleepDuration.getTimeDelta(age, isMale);
		final TimeDelta delta = SleepDelta.getDelta(accumDebt, exhaustionLevel, sleepQuality);
		
		return start.add(base).add(delta);
	}
	
	/**
	 * Returns the list of days in a week, starting at the time in the start parameter
	 * @param start the time when the week starts
	 * @return the list of week days the correspond to the week
	 */
	static List<WeekDay> getWeek(DateTime start) {
		final List<WeekDay> week = new ArrayList<WeekDay>(7);
		for (int i = 0; i < 7; i++) {
			week.add(new WeekDay(start, start.add(DAY).add(MINUS_1_SECOND)));
			start = start.add(DAY);
		}
		
		return week;
	}
}

/**
 * Represents a week day
 */
class WeekDay {
	final DateTime from;
	final DateTime until;

	public WeekDay(final DateTime from, final DateTime until) {
		super();
		this.from = from;
		this.until = until;
	}
}

/**
 * Represents a day in the bar chart
 */
class ChartDay {
	final DateTime from;
	final DateTime until;
	final TimeDelta debt;
	final TimeDelta accumulatedDebt;
	final DateTime optimumWakingTime;
	final List<DayRecord> records;
	
	public ChartDay(final DateTime from, final DateTime until, final TimeDelta debt, final TimeDelta accumulatedDebt, final DateTime optimumWakingTime, final List<DayRecord> records) {
		super();
		this.from = from;
		this.until = until;
		this.debt = debt;
		this.accumulatedDebt = accumulatedDebt;
		this.optimumWakingTime = optimumWakingTime;
		this.records = records;
	}
}

/**
 * Enumeration of the bar chart series types
 */
enum SeriesType {
	WAKE,
	SLEEP,
	UNDERSLEEP,
	OVERSLEEP
}

/**
 * Represents a series value
 */
class SeriesValue {
	final long value;
	final SeriesType type;

	public SeriesValue(final long value, final SeriesType type) {
		super();
		this.value = value;
		this.type = type;
	}
}