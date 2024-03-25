package org.rsa.adventure;

import org.rsa.adventure.model.ActivityPerformResponse;

import java.util.HashMap;
import java.util.Map;

public class TravelSummaryManager {

    private static final Map<String, ActivityPerformResponse> travelSummaryMap = new HashMap<>();

    public static ActivityPerformResponse getUserSummary(String userId) {
        return travelSummaryMap.getOrDefault(userId, new ActivityPerformResponse());
    }

    public static void createNewTravelSummary(String userId) {
        travelSummaryMap.put(userId, new ActivityPerformResponse());
    }

    public static void updateTravelSummary(String userId, ActivityPerformResponse summary) {
        travelSummaryMap.put(userId, summary);
    }

    public static void clearTravelSummary(String userId) {
        travelSummaryMap.remove(userId);
    }
}
