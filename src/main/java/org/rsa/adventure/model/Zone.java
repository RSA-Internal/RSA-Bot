package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Zone {

    START_TOWN(0, "Starter Town", Collections.emptyList(), Collections.emptyMap()),
    FOREST(1, "Forest",
        List.of(Activity.HUNT, Activity.FORAGE, Activity.LEAVE),
        Map.of(Skill.HUNTING, 0, Skill.FORAGING, 0)),
    RIVER(2, "River",
        List.of(Activity.FISH, Activity.RELAX, Activity.LEAVE),
        Map.of(Skill.FISHING, 0)),
    CAVE(3, "Cave",
        List.of(Activity.MINE, Activity.FORAGE, Activity.LEAVE),
        Map.of(Skill.MINING, 0)),
    FARM(4, "Farm",
        List.of(Activity.FORAGE, Activity.LEAVE),
        Map.of(Skill.FORAGING, 2))
    ;

    private final Integer id;
    private final String name;
    private final List<Activity> activities;
    private final Map<Skill, Integer> requiredSkills;

    private static Stream<Zone> zoneStream() {
        return Arrays.stream(Zone.values());
    }

    public static Zone getById(int id) {
        return zoneStream().filter(zone -> zone.id.equals(id)).findFirst().orElse(null);
    }
}
