package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Activity {
    LEAVE(0, "Leave", 0, 0,
        Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap()),
    HUNT(1, "Hunt Animals", 2, 1,
        Map.of(Skill.HUNTING, 0), List.of(Item.BASIC_KNIFE),
        Map.of(
            Item.BONE, new ItemDrop(2, 4.5),
            Item.ANIMAL_PELT, new ItemDrop(1, 0.5),
            Item.RAW_MEAT, new ItemDrop(1, 95.0)
        )),
    FORAGE(2, "Forage", 2, 1,
        Map.of(Skill.FORAGING, 0), List.of(),
        Map.of(
            Item.BERRY, new ItemDrop(3, 25.0),
            Item.STICK, new ItemDrop(2, 25.0),
            Item.PLANT_FIBER, new ItemDrop(4, 50.0)
        )),
    FISH(3, "Fish", 2, 1,
        Map.of(Skill.FISHING, 0), List.of(),
        Map.of(
            Item.STICK, new ItemDrop(1, 10.0),
            Item.BONE, new ItemDrop(1, 1.0),
            Item.KELP, new ItemDrop(2, 45.0),
            Item.RAW_FISH, new ItemDrop(1, 44.0)
        )),
    RELAX(4, "Relax", 0, 0,
        Map.of(Skill.NO_SKILL, 0), Collections.emptyList(), Map.of()),
    MINE(5, "Mine", 4, 1,
        Map.of(Skill.MINING, 0), List.of(),
        Map.of(
            Item.ROCK, new ItemDrop(4, 100.0)
        )),
    FARM(6, "Farm", 3, 1,
        Map.of(Skill.FORAGING, 2), List.of(Item.BASIC_HOE),
        Map.of(
            Item.BERRY, new ItemDrop(2, 30.0),
            Item.CARROT, new ItemDrop(4, 40.0),
            Item.POTATO, new ItemDrop(3, 20.0),
            Item.PLANT_FIBER, new ItemDrop(2, 10.0)
        )),
    CHOP(7, "Chop Tree", 4, 1,
        Map.of(Skill.FORAGING, 1), List.of(Item.BASIC_AXE),
        Map.of(
            Item.LOG, new ItemDrop(4, 100.0)
        )),
    ;

    private final Integer id;
    private final String name;
    private final Integer staminaRequirement;
    private final Integer experienceGainBound;
    private final Map<Skill, Integer> requiredSkillSet;
    private final List<Item> requiredItems;
    private final Map<Item, ItemDrop> possibleItems;

    public ActivityResponse userCanPerformActivity(UserAdventureProfile profile) {
        Map<Integer, Integer> skillSetLevel = profile.getSkillSetLevel();

        for (Map.Entry<Skill, Integer> requiredEntry : requiredSkillSet.entrySet()) {
            Skill skill = requiredEntry.getKey();
            int skillId = skill.getId();
            int requiredLevel = requiredEntry.getValue();
            int playerLevel = skillSetLevel.get(skillId);
            if (playerLevel < requiredLevel) return new ActivityResponse(false, String.join(" ", skill.getName(), String.valueOf(playerLevel), "/", String.valueOf(requiredLevel)));
        }

        for (Item item : requiredItems) {
            BigInteger itemCount = profile.getBackpack().getOrDefault(item.getId(), BigInteger.ZERO);
            if (itemCount.signum() == 0) return new ActivityResponse(false, "Missing " + item.getName());
        }

        return new ActivityResponse(true, "success");
    }
}