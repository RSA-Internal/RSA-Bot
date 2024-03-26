package org.rsa.adventure.model;

import lombok.Getter;
import org.rsa.entity.adventure.ItemEntity;
import org.rsa.entity.adventure.SkillEntity;
import org.rsa.entity.adventure.ZoneEntity;

import java.util.*;

@Getter
public class ActivityPerformResponse {
    private final List<SkillEntity> skillsLeveledUp;
    private final Map<SkillEntity, Integer> experienceGained;
    private final Map<ItemEntity, Integer> itemsReceived;
    private final Set<ZoneEntity> unlockedZones;
    private final List<String> messages;

    public ActivityPerformResponse() {
        skillsLeveledUp = new ArrayList<>();
        experienceGained = new HashMap<>();
        itemsReceived = new HashMap<>();
        unlockedZones = new HashSet<>();
        messages = new ArrayList<>();
    }

    public void addSkillLeveledUp(SkillEntity skill) {
        skillsLeveledUp.add(skill);
    }

    public void addExperienceGained(SkillEntity skill, int gain) {
        int previousGain = experienceGained.getOrDefault(skill, 0);
        experienceGained.put(skill, previousGain + gain);
    }

    public void addItemReceived(ItemEntity item, int count) {
        int previousCount = itemsReceived.getOrDefault(item, 0);
        itemsReceived.put(item, previousCount + count);
    }

    public void addUnlockedZone(ZoneEntity zone) {
        unlockedZones.add(zone);
    }

    public void addMessage(String message) {
        messages.add(message);
    }
}
