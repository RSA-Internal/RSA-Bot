package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Activity {
    LEAVE(0, "Leave", 0, 0, Collections.emptyMap(), Collections.emptyList()),
    HUNT(1, "Hunt Animals", 2, 1, Map.of(Skill.HUNTING, 0), List.of(Item.BASIC_KNIFE)),
    FORAGE(2, "Forage", 2, 1, Map.of(Skill.FORAGING, 0), List.of()),
    FISH(3, "Fish", 2, 1, Map.of(Skill.FISHING, 0), List.of()),
    RELAX(4, "Relax", 0, 0, Map.of(Skill.NO_SKILL, 0), Collections.emptyList()),
    MINE(5, "Mine", 4, 1, Map.of(Skill.MINING, 0), List.of()),
    FARM(6, "Farm", 3, 1, Map.of(Skill.FORAGING, 2), List.of(Item.BASIC_HOE)),
    CHOP(7, "Chop Tree", 4, 1, Map.of(Skill.FORAGING, 1), List.of(Item.BASIC_AXE)),
    ;

    private final Integer id;
    private final String name;
    private final Integer staminaRequirement;
    private final Integer experienceGainBound;
    private final Map<Skill, Integer> requiredSkillSet;
    private final List<Item> requiredItems;
}
