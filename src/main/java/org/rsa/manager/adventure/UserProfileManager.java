package org.rsa.manager.adventure;

import org.rsa.aws.RequestsManager;
import org.rsa.model.adventure.profile.CachedUserProfile;
import org.rsa.model.adventure.profile.SavedUserProfile;

import java.util.HashMap;
import java.util.Map;

public class UserProfileManager {

    /*
        Need to track active profiles
        Need to spawn thread responsible for saving profiles regularly
            - Base on time or changes
        Provide profile from cache or load
     */

    private final static String TABLE_NAME = "user_adventure_profiles";
    private final static RequestsManager<SavedUserProfile> requestManager = new RequestsManager<>(TABLE_NAME, SavedUserProfile.class);
    private final static Map<String, CachedUserProfile> activeUserProfiles = new HashMap<>();
}
