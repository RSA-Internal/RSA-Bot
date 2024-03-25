package org.rsa.adventure;

import org.rsa.adventure.model.ActivityPerformResponse;

import java.util.HashMap;
import java.util.Map;

public class TravelSummaryManager {

    private static final Map<String, ActivityPerformResponse> travelSummaryMap = new HashMap<>();

    public static ActivityPerformResponse getUserSummary(String userId) {
        return travelSummaryMap.getOrDefault(userId, createNewTravelSummary(userId));
    }

    public static ActivityPerformResponse createNewTravelSummary(String userId) {
        ActivityPerformResponse response = new ActivityPerformResponse();
        travelSummaryMap.put(userId, response);
        return response;
    }

    public static void updateTravelSummary(String userId, ActivityPerformResponse summary) {
        travelSummaryMap.put(userId, summary);
    }

    public static void clearTravelSummary(String userId) {
        travelSummaryMap.remove(userId);
    }
}
