package pt.isec.gps.g22.sleeper.core.time;

public class TimeOfDay {

	private final int value;
	
	private TimeOfDay(final int value) {
		this.value = value;
	}
	
	public int getHours() {
		return value / 60 / 60;
	}
	
	public int getMinutes() {
		return (value / 60) - getHours() * 60;
	}
	
	// public static 
	
}
