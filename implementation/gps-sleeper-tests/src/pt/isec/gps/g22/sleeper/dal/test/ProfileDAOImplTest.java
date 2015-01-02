package pt.isec.gps.g22.sleeper.dal.test;

import pt.isec.gps.g22.sleeper.core.Profile;
import pt.isec.gps.g22.sleeper.dal.ProfileDAOImpl;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

public class ProfileDAOImplTest extends InstrumentationTestCase {
    private ProfileDAOImpl profileDAOImpl;

    public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getInstrumentation().getTargetContext(), "test_");
        profileDAOImpl = new ProfileDAOImpl(context);
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInsertValidProfile() throws Exception {
        Profile profile = new Profile(1,488937600,300);
        assertEquals("Empty profile",1,profileDAOImpl.insertProfile(profile));
    }

    public void testLoadProfile() throws Exception {
        Profile newProfile = new Profile(1,488937600,300);
        profileDAOImpl.insertProfile(newProfile);

        Profile loadedProfile = profileDAOImpl.loadProfile();

        assertEquals("Profile Not Loaded", loadedProfile.getDateOfBirth(), loadedProfile.getDateOfBirth());
    }

    public void testUpdateProfile() throws Exception {
        Profile newProfile = new Profile(1,488937600,300);
        profileDAOImpl.insertProfile(newProfile);

        Profile existentProfile = profileDAOImpl.loadProfile();
        existentProfile.setDateOfBirth(488937601);
        profileDAOImpl.updateProfile(existentProfile);

        Profile updatedProfile = profileDAOImpl.loadProfile();
        assertEquals("Not Updated", updatedProfile.getDateOfBirth(), existentProfile.getDateOfBirth());
    }
}