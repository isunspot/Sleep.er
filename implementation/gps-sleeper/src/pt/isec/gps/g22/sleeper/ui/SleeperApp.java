package pt.isec.gps.g22.sleeper.ui;

import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.dal.DayRecordDAOImpl;
import pt.isec.gps.g22.sleeper.dal.ProfileDAOImpl;
import android.app.Application;
import android.util.Log;

public class SleeperApp extends Application{
	ProfileDAOImpl profileDAOImpl;
	DayRecordDAOImpl dayRecordDAOImpl;
	Profile profile = null;
	boolean profileDefined;
	
	public SleeperApp() {
		profileDAOImpl = new ProfileDAOImpl(SleeperApp.this);
		dayRecordDAOImpl = new DayRecordDAOImpl(SleeperApp.this);
		profileDefined = false;
	}
	
	public void loadProfile() {
		profileDAOImpl.loadProfile();
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
	
	public ProfileDAOImpl getProfileDAOImpl() {
		return profileDAOImpl;
	}
}
