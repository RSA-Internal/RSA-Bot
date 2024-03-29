package org.rsa.model.adventure.entity;

import lombok.Getter;
import org.rsa.entity.adventure.ActivityEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public enum Zone {

    START_TOWN(0, "Starter Town", Collections.emptyList(), Collections.emptyMap()),
    FOREST(1, "Forest",
        List.of(
            ActivityEntity.fromEnum(Activity.HUNT),
            ActivityEntity.fromEnum(Activity.FORAGE),
            ActivityEntity.fromEnum(Activity.CHOP),
            ActivityEntity.fromEnum(Activity.LEAVE)
        ),
        Map.of(Skill.HUNTING, 0, Skill.FORAGING, 0)),
    RIVER(2, "River",
        List.of(
            ActivityEntity.fromEnum(Activity.FISH),
            ActivityEntity.fromEnum(Activity.LEAVE)
        ),
        Map.of(Skill.FISHING, 0)),
    CAVE(3, "Cave",
        List.of(
            ActivityEntity.fromEnum(Activity.MINE),
            ActivityEntity.fromEnum(Activity.LEAVE)
        ),
        Map.of(Skill.MINING, 0)),
    FARM(4, "Farm",
        List.of(
            ActivityEntity.fromEnum(Activity.FORAGE),
            ActivityEntity.fromEnum(Activity.LEAVE)
        ),
        Map.of(Skill.FORAGING, 2))
    ;

    private final Integer id;
    private final String name;
    private final List<ActivityEntity> activities;
    private final Map<Skill, Integer> requiredSkills;

    Zone(Integer id, String name, List<ActivityEntity> activityEntities, Map<Skill, Integer> requiredSkills) {
        this.id = id;
        this.name = name;
        this.activities = activityEntities;
        this.requiredSkills = requiredSkills;
    }
}
