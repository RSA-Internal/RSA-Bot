package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

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
        List.of(Activity.HUNT, Activity.FORAGE, Activity.CHOP, Activity.LEAVE),
        Map.of(Skill.HUNTING, 0, Skill.FORAGING, 0)),
    RIVER(2, "River",
        List.of(Activity.FISH, Activity.RELAX, Activity.LEAVE),
        Map.of(Skill.FISHING, 0)),
    CAVE(3, "Cave",
        List.of(Activity.MINE, Activity.LEAVE),
        Map.of(Skill.MINING, 0)),
    FARM(4, "Farm",
        List.of(Activity.FORAGE, Activity.LEAVE),
        Map.of(Skill.FORAGING, 2))
    ;

    private final Integer id;
    private final String name;
    private final List<Activity> activities;
    private final Map<Skill, Integer> requiredSkills;

    public static Stream<Zone> zoneStream() {
        return Arrays.stream(Zone.values());
    }

    public static List<SelectOption> getZoneOptionList() {
        return getZoneOptionList(1);
    }

    public static List<SelectOption> getZoneOptionList(int defaultIndex) {
        return zoneStream()
            .filter(zone -> zone.id > 0)
            .map(zone ->
                SelectOption
                    .of(zone.name, "zone-" + zone.id)
                    .withDescription("")
                    .withDefault(zone.id == defaultIndex))
            .toList();
    }

    public static Zone getById(int id) {
        return zoneStream().filter(zone -> zone.id.equals(id)).findFirst().orElse(null);
    }

    public String getAsDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n- ID: ");
        builder.append(id);
        builder.append("\n- Name: ");
        builder.append(name);
        builder.append("\n- Activities");
        for (Activity activity : activities) {
            builder.append("\n - ");
            builder.append(activity.getName());
        }
        builder.append("\n- Unlock Requirements");
        List<Skill> skillsRequiredAboveZero = requiredSkills.keySet().stream().filter(skill -> requiredSkills.get(skill) > 0).toList();
        if (!skillsRequiredAboveZero.isEmpty()) {
            for (Skill skill : skillsRequiredAboveZero) {
                builder.append("\n - ");
                builder.append(skill.getName());
                builder.append(" [Level ");
                builder.append(requiredSkills.get(skill));
                builder.append("]");
            }
        } else {
            builder.append("\n - None");
        }

        return builder.toString();
    }
}
