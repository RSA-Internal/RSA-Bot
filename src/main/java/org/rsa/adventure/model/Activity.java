package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Activity {
    LEAVE(0, "Leave", 0, 0, 0, Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap()),
    HUNT(1, "Hunt Animals", 2, 5, 2,
        List.of(Item.BASIC_KNIFE),
        Map.of(
            Item.NOTHING, new ItemDrop(1, 40),
            Item.BONE, new ItemDrop(2, 4),
            Item.ANIMAL_PELT, new ItemDrop(1, 1),
            Item.RAW_MEAT, new ItemDrop(1, 55)),
        Map.of(Skill.HUNTING, 0)),
    FORAGE(2, "Forage", 2, 3, 3,
        List.of(),
        Map.of(
            Item.NOTHING, new ItemDrop(1, 40),
            Item.BERRY, new ItemDrop(3, 13),
            Item.STICK, new ItemDrop(2, 17),
            Item.PLANT_FIBER, new ItemDrop(4, 20),
            Item.ROCK, new ItemDrop(1, 10)),
        Map.of(Skill.FORAGING, 0)),
    FISH(3, "Fish", 2, 2, 1,
        List.of(Item.BASIC_FISHING_ROD),
        Map.of(
            Item.NOTHING, new ItemDrop(1, 40),
            Item.STICK, new ItemDrop(1, 10),
            Item.BONE, new ItemDrop(1, 1),
            Item.KELP, new ItemDrop(2, 30),
            Item.RAW_FISH, new ItemDrop(1, 19)),
        Map.of(Skill.FISHING, 0)),
    RELAX(4, "Relax", 0, 0, 0, Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap()),
    MINE(5, "Mine", 4, 5, 2,
        List.of(Item.BASIC_PICKAXE),
        Map.of(Item.ROCK, new ItemDrop(4, 100)),
        Map.of(Skill.MINING, 0)),
    FARM(6, "Farm", 3, 3, 3,
        List.of(Item.BASIC_HOE),
        Map.of(
            Item.NOTHING, new ItemDrop(1, 30),
            Item.BERRY, new ItemDrop(2, 15),
            Item.CARROT, new ItemDrop(4, 25),
            Item.POTATO, new ItemDrop(3, 15),
            Item.PLANT_FIBER, new ItemDrop(2, 6),
            Item.ROCK, new ItemDrop(2, 9)),
        Map.of(Skill.FORAGING, 2)),
    CHOP(7, "Chop Tree", 4, 4, 1,
        List.of(Item.BASIC_AXE),
        Map.of(Item.LOG, new ItemDrop(4, 100)),
        Map.of(Skill.FORAGING, 1, Skill.LOGGING, 0)),
    ;

    private final Integer id;
    private final String name;
    private final Integer staminaRequirement;
    private final Integer experienceGainBound;
    private final Integer rewardRolls;
    private final List<Item> requiredItems;
    private final Map<Item, ItemDrop> possibleItems;
    private final Map<Skill, Integer> requiredSkillSet;
}