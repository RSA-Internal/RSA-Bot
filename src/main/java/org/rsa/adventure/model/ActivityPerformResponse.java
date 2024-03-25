package org.rsa.adventure.model;

import lombok.Getter;

import java.util.*;

@Getter
public class ActivityPerformResponse {
    private final List<Skill> skillsLeveledUp;
    private final Map<Skill, Integer> experienceGained;
    private final Map<Item, Integer> itemsReceived;

    public ActivityPerformResponse() {
        skillsLeveledUp = new ArrayList<>();
        experienceGained = new HashMap<>();
        itemsReceived = new HashMap<>();
    }

    public void addSkillLeveledUp(Skill skill) {
        skillsLeveledUp.add(skill);
    }

    public void addExperienceGained(Skill skill, int gain) {
        int previousGain = experienceGained.getOrDefault(skill, 0);
        experienceGained.put(skill, previousGain + gain);
    }

    public void addItemReceived(Item item, int count) {
        int previousCount = itemsReceived.getOrDefault(item, 0);
        itemsReceived.put(item, previousCount + count);
    }
}
