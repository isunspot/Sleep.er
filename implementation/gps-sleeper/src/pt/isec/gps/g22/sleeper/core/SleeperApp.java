package pt.isec.gps.g22.sleeper.core;

import pt.isec.gps.g22.sleeper.dal.DayRecordDAO;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAOImpl;
import pt.isec.gps.g22.sleeper.dal.ProfileDAO;
import pt.isec.gps.g22.sleeper.dal.ProfileDAOImpl;
import android.app.Application;

public class SleeperApp extends Application {
	private ProfileDAO profileDAO;
	private DayRecordDAO dayRecordDAO;
	private Profile profile = null;
	private boolean profileDefined;
	
	public SleeperApp() {
		profileDAO = new ProfileDAOImpl(SleeperApp.this);
		dayRecordDAO = new DayRecordDAOImpl(SleeperApp.this);
		profileDefined = false;
	}
	
	public void loadProfile() {
		profileDAO.loadProfile();
		if(profile != null)
			profileDefined = true;
		else
			profile = new Profile();
	}
	
	public boolean profileDefined() {
		return profileDefined;
	}
	
	public void defineProfile() {
		profileDefined = true;
	}
	
	public Profile getProfile() {
		return profile;
	}
	
	public ProfileDAO getProfileDAO() {
		return profileDAO;
	}

	public DayRecordDAO getDayRecordDAO() {
		return dayRecordDAO;
	}
	
}
