package org.rsa.adventure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CooldownTracker {

    private static final Logger logger = LoggerFactory.getLogger(CooldownTracker.class);
    public static final long ACTIVITY_COOLDOWN = 3000; // 3s
    public static final Map<String, Long> USER_COOLDOWN_MAP = new HashMap<>();

    public static void setUserCooldown(String userId, Long millis) {
        logger.info("Setting cooldown for {} - time: {}.", userId, millis);
        USER_COOLDOWN_MAP.put(userId, millis);
    }

    public static long isCooldownReady(String userId) {
        logger.info("Checking cooldown remaining for {}.", userId);
        if (USER_COOLDOWN_MAP.containsKey(userId)) {
            Long userLastCooldown = USER_COOLDOWN_MAP.get(userId);
            long currentTime = System.currentTimeMillis();

            long remaining = ACTIVITY_COOLDOWN - (currentTime - userLastCooldown);
            logger.info("Remaining cooldown for {}: {}.", userId, remaining);
            return remaining;
        }
        logger.info("User {} has no cooldown.", userId);
        return -1;
    }
}
