package pt.isec.gps.g22.sleeper.ui;

import java.util.ArrayList;
import java.util.List;

import pt.isec.gps.g22.sleeper.core.DayRecord;
import pt.isec.gps.g22.sleeper.core.Profile;

import static pt.isec.gps.g22.sleeper.ui.WeeklyViewUtils.duration;

class WeekDay {
	final int from;
	final int until;

	public WeekDay(final int from, final int until) {
		super();
		this.from = from;
		this.until = until;
	}
}

class ChartDay {
	final int debt;
	final int accumulatedDebt;
	final int optimumWakingTime;
	final List<DayRecord> records;
	
	public ChartDay(int debt, int accumulatedDebt, int optimumWakingTime, List<DayRecord> records) {
		super();
		this.debt = debt;
		this.accumulatedDebt = accumulatedDebt;
		this.optimumWakingTime = optimumWakingTime;
		this.records = records;
	}
}

public class WeeklyViewUtils {
	
	static final int DAY_SECONDS = 24 * 60 * 60;
	
	static List<List<SeriesValue>> recordsToSeries(final Profile profile, final List<DayRecord> records, final List<WeekDay> weekDays) {
		final ChartDay[] chartDays = new ChartDay[weekDays.size()];
		
		int accumDebt = 0;
		for(int i = 0; i < records.size(); i++) {
			final WeekDay weekDay = weekDays.get(i); 
			List<DayRecord> dayRecords = new ArrayList<DayRecord>();
			// Determine which records belong to the day
			for (DayRecord record : records) {
				if (record.getSleepDate() >= weekDay.from && record.getSleepDate() <= weekDay.until) { 
					dayRecords.add(record);
				}
			}
			
			final ExhaustionLevel exhaustionLevel = averageExhaustionLevel(dayRecords);
			final SleepQuality sleepQuality = averageSleepQuality(dayRecords);
			
			int optimumWakingTime = 0;
			int debt = 0;
			if (records.size() == 0) { // didn't sleep
				optimumWakingTime = optimumWakingTime(weekDay.from, profile, accumDebt, exhaustionLevel, sleepQuality);
				debt = optimumWakingTime - weekDay.from;
			} else if (records.size() == 1) { // 1 sleep period
				optimumWakingTime = optimumWakingTime(dayRecords.get(0).getSleepDate(), profile, accumDebt, exhaustionLevel, sleepQuality);
				debt = optimumWakingTime - dayRecords.get(0).getWakeupDate();
			} else { // multiple sleep periods
				final int sleepSum = sleepSum(records); // Time slept during the day
				int wakeTimeSinceDayStart = optimumWakingTime(weekDay.from, profile, accumDebt, exhaustionLevel, sleepQuality);
				int lastPeriodDebt = (wakeTimeSinceDayStart - weekDay.from) - sleepSum;
				debt = dayRecords.get(dayRecords.size() - 1).getWakeupDate() + lastPeriodDebt;
				optimumWakingTime = dayRecords.get(dayRecords.size() - 1).getSleepDate() + lastPeriodDebt;
			}

			chartDays[i] = new ChartDay(debt, accumDebt, optimumWakingTime, dayRecords);
			
			accumDebt += debt;
		}
		
		final List<List<SeriesValue>> dayValuesList = new ArrayList<List<SeriesValue>>();
		for(final ChartDay chartDay : chartDays) {
			final List<SeriesValue> dayValues = new ArrayList<SeriesValue>();
			final int recordCount = chartDay.records.size();
			
			if (recordCount == 1) {
				dayValues.addAll(recordValues(chartDay.records.get(0), chartDay.optimumWakingTime));
			} else if (recordCount > 1) {
				final int lastRecordIndex = recordCount - 1;
				for (int i = 0; i < recordCount; i++) {
					final DayRecord dayRecord = chartDay.records.get(i);
					final boolean isLast = i == lastRecordIndex;
					
					if (isLast) {
						dayValues.addAll(recordValues(dayRecord, chartDay.optimumWakingTime));
					} else {
						dayValues.addAll(recordValues(dayRecord, dayRecord.getWakeupDate()));
					}
				}
			}
			
			dayValuesList.add(dayValues);
		}

		return dayValuesList; 
	}
	
	static int sleepSum(final List<DayRecord> records) {
		int sleepSum = 0;
		for (DayRecord record : records) {
			sleepSum += record.getWakeupDate() - record.getSleepDate();
		}
		
		return sleepSum;
	}
	
	static ExhaustionLevel averageExhaustionLevel(final List<DayRecord> records) {
		int exaustion = 0;
		int count = 0;
		
		for(final DayRecord record : records) {
			if (record.getExhaustion() > 0) {
				count++;
				exaustion += record.getExhaustion();
			}
		}
		
		return ExhaustionLevel.fromInt(exaustion / count);
	}
	
	static SleepQuality averageSleepQuality(final List<DayRecord> records) {
		int quality = 0;
		int count = 0;
		
		for(final DayRecord record : records) {
			if (record.getSleepQuality() > 0) {
				count++;
				quality += record.getSleepQuality();
			}
		}
		
		return SleepQuality.fromInt(quality / count);
	}
	
	static List<SeriesValue> recordValues(final DayRecord record, final int optimumWakingTime) {
		final List<SeriesValue> values = new ArrayList<SeriesValue>();
		
		values.add(new SeriesValue(record.getSleepDate(), SeriesType.SLEEP));
		if (optimumWakingTime < record.getWakeupDate()) { // oversleep
			values.add(new SeriesValue(optimumWakingTime, SeriesType.OVERSLEEP));
			values.add(new SeriesValue(record.getWakeupDate(), SeriesType.WAKE));
		} else if (optimumWakingTime > record.getWakeupDate()) { // undersleep
			values.add(new SeriesValue(record.getWakeupDate(), SeriesType.UNDERSLEEP));
			values.add(new SeriesValue(optimumWakingTime, SeriesType.WAKE));
		} else {
			values.add(new SeriesValue(record.getWakeupDate(), SeriesType.WAKE));
		}
		
		return values;
	}
	
	static int optimumWakingTime(final int start, final Profile profile, final int accumDebt, final ExhaustionLevel exhaustionLevel, 
			final SleepQuality sleepQuality) {
		return start + duration(8); // TODO implement
	}
	
	static List<WeekDay> getWeek(final int start) {
		final List<WeekDay> week = new ArrayList<WeekDay>(7);
		int seconds = start;
		for (int i = 0; i < 7; i++) {
			week.add(new WeekDay(seconds, seconds + DAY_SECONDS - 1));
			seconds += DAY_SECONDS;
		}
		
		return week;
	}
	
	static int duration(final int hours) {
		return duration(hours, 0);
	}
	
	static int duration(final int hours, final int minutes) {
		return hours * 60 + minutes;
	}
	
}

enum SeriesType {
	WAKE,
	SLEEP,
	UNDERSLEEP,
	OVERSLEEP
}

enum ExhaustionLevel {
	LOW(1),
	MEDIUM(2),
	HIGH(3);
	
	final int level;

	private ExhaustionLevel(int level) {
		this.level = level;
	}
	
	static ExhaustionLevel fromInt(final int level) {
		switch (level) {
		case 1: return LOW;
		case 2: return MEDIUM;
		case 3: return HIGH;
		default: throw new IllegalArgumentException("Invalid level");
		}
	}
}

enum SleepQuality {
	LOW(1),
	MEDIUM(2),
	HIGH(3);
	
	final int level;

	private SleepQuality(int level) {
		this.level = level;
	}
	
	static SleepQuality fromInt(final int quality) {
		switch (quality) {
		case 1: return LOW;
		case 2: return MEDIUM;
		case 3: return HIGH;
		default: throw new IllegalArgumentException("Invalid quality");
		}
	}
}

class SleepDuration {
	final int minAge, maxAge, minMale, maxMale, minFemale, maxFemale;
	
	private SleepDuration(int minAge, int maxAge, int minMale,
			int maxMale, int minFemale, int maxFemale) {
		this.minAge = minAge;
		this.maxAge = maxAge;
		this.minMale = minMale;
		this.maxMale = maxMale;
		this.minFemale = minFemale;
		this.maxFemale = maxFemale;
	}
	
	private static SleepDuration[] durations;
	
	boolean contains(final int age) {
		return age >= minAge && age <= maxAge;
	}
	
	int meanDuration(final boolean male) {
		return male ? mean(minMale, maxMale) : mean(minFemale, maxFemale);
	}
	
	static int mean(final int min, final int max) {
		return (min + max) / 2;
	}
	

	
	static {
		durations = new SleepDuration[3];
		durations[0] = new SleepDuration(13, 18, WeeklyViewUtils.duration(8), duration(9), duration(8), duration(9));
		durations[1] = new SleepDuration(19, 65, duration(7), duration(8), duration(7, 30), duration(8, 30));
		durations[2] = new SleepDuration(66, 120, duration(8), duration(9), duration(8, 30), duration(9, 30));
	}
	
	static float getDuration(final int age, final boolean male) {
		for(int i = 0; i < durations.length; i++) {
			final SleepDuration duration = durations[i];
			if (duration.contains(age)) {
				return duration.meanDuration(male);
			}
		}
		
		throw new IllegalArgumentException("Invalid age");
	}
	
}

class ExhaustionLevelDelta {
	
}



class SeriesValue {
	final int value;
	final SeriesType type;

	public SeriesValue(final int value, final SeriesType type) {
		super();
		this.value = value;
		this.type = type;
	}
}