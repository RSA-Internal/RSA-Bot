package org.rsa.adventure;

import org.rsa.adventure.model.Zone;

import java.util.HashMap;
import java.util.Map;

public class UserZoneManager {
    private static final Map<String, Integer> userZoneMap = new HashMap<>();

    public static void userTravelToZone(String userId, Integer zoneId) {
        userZoneMap.put(userId, zoneId);
    }

    public static int getUserCurrentZone(String userId) {
        return userZoneMap.getOrDefault(userId, Zone.START_TOWN.getId());
    }
}
