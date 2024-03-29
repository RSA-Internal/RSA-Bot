package org.rsa.manager.adventure;

import org.rsa.model.adventure.response.ActivityPerformResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TravelSummaryManager {

    private static final Logger logger = LoggerFactory.getLogger(TravelSummaryManager.class);
    private static final Map<String, ActivityPerformResponse> travelSummaryMap = new HashMap<>();

    public static ActivityPerformResponse getUserSummary(String userId) {
        logger.info("Getting travel summary for {}", userId);
        ActivityPerformResponse summary = travelSummaryMap.get(userId);
        if (summary == null) {
            logger.info("Summary does not exist for {}", userId);
            summary = createNewTravelSummary(userId);
        }

        return summary;
    }

    public static ActivityPerformResponse createNewTravelSummary(String userId) {
        logger.info("Creating travel summary for {}", userId);
        ActivityPerformResponse response = new ActivityPerformResponse();
        travelSummaryMap.put(userId, response);
        return response;
    }

    public static void updateTravelSummary(String userId, ActivityPerformResponse summary) {
        logger.info("Updating travel summary for {}", userId);
        travelSummaryMap.put(userId, summary);
    }

    public static void clearTravelSummary(String userId) {
        logger.info("Clearing travel summary for {}", userId);
        travelSummaryMap.remove(userId);
    }
}
