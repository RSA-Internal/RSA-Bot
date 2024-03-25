package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rsa.adventure.TravelSummaryManager;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Activity {
    LEAVE(0, "Leave", 0, 0,
        Collections.emptyMap(), Collections.emptyList(), 0, Collections.emptyMap()),
    HUNT(1, "Hunt Animals", 2, 1,
        Map.of(Skill.HUNTING, 0), List.of(Item.BASIC_KNIFE),
        2, Map.of(
            Item.BONE, new ItemDrop(2, 4),
            Item.ANIMAL_PELT, new ItemDrop(1, 1),
            Item.RAW_MEAT, new ItemDrop(1, 95)
        )),
    FORAGE(2, "Forage", 2, 1,
        Map.of(Skill.FORAGING, 0), List.of(),
        3, Map.of(
            Item.BERRY, new ItemDrop(3, 25),
            Item.STICK, new ItemDrop(2, 25),
            Item.PLANT_FIBER, new ItemDrop(4, 50)
        )),
    FISH(3, "Fish", 2, 1,
        Map.of(Skill.FISHING, 0), List.of(),
        1, Map.of(
            Item.STICK, new ItemDrop(1, 10),
            Item.BONE, new ItemDrop(1, 1),
            Item.KELP, new ItemDrop(2, 45),
            Item.RAW_FISH, new ItemDrop(1, 44)
        )),
    RELAX(4, "Relax", 0, 0,
        Map.of(Skill.NO_SKILL, 0), Collections.emptyList(), 0, Collections.emptyMap()),
    MINE(5, "Mine", 4, 1,
        Map.of(Skill.MINING, 0), List.of(Item.BASIC_PICKAXE),
        2, Map.of(
            Item.ROCK, new ItemDrop(4, 100)
        )),
    FARM(6, "Farm", 3, 1,
        Map.of(Skill.FORAGING, 2), List.of(Item.BASIC_HOE),
        3, Map.of(
            Item.BERRY, new ItemDrop(2, 30),
            Item.CARROT, new ItemDrop(4, 40),
            Item.POTATO, new ItemDrop(3, 20),
            Item.PLANT_FIBER, new ItemDrop(2, 10)
        )),
    CHOP(7, "Chop Tree", 4, 1,
        Map.of(Skill.FORAGING, 1), List.of(Item.BASIC_AXE),
        1, Map.of(
            Item.LOG, new ItemDrop(4, 100)
        )),
    ;

    private final Integer id;
    private final String name;
    private final Integer staminaRequirement;
    private final Integer experienceGainBound;
    private final Map<Skill, Integer> requiredSkillSet;
    private final List<Item> requiredItems;
    private final Integer rewardRolls;
    private final Map<Item, ItemDrop> possibleItems;

    public static Stream<Activity> activityStream() {
        return Arrays.stream(Activity.values());
    }

    public static Activity getById(int id) {
        return activityStream().filter(activity -> activity.id.equals(id)).findFirst().orElse(null);
    }

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

    public ActivityPerformResponse perform(UserAdventureProfile profile) {
        Random random = new Random();
        ActivityPerformResponse travelSummary = TravelSummaryManager.getUserSummary(profile.getUserid());
        ActivityPerformResponse response = new ActivityPerformResponse();

        // Increment time performed
        profile.performActivity(this);

        // Experience gain for skill
        for (Skill skill : requiredSkillSet.keySet()) {
            int maxExperience = experienceGainBound;
            int gainedExperience = 1;
            if (maxExperience > 1) {
                gainedExperience = random.nextInt(1, maxExperience);
            }
            travelSummary.addExperienceGained(skill, gainedExperience);
            response.addExperienceGained(skill, gainedExperience);
            boolean skillLeveledUp = profile.updateSkillSet(skill, gainedExperience);
            if (skillLeveledUp) {
                travelSummary.addSkillLeveledUp(skill);
                response.addSkillLeveledUp(skill);
            }
        }

        // Item roll
        Item[] items = generateItemArray(possibleItems);
        int rolls = random.nextInt(1, rewardRolls);

        for (int i=0;i<rolls;i++) {
            Item randomReward = items[random.nextInt(items.length)];
            ItemDrop itemDropForReward = possibleItems.get(randomReward);
            int rewardCount = random.nextInt(1, itemDropForReward.dropMax());
            travelSummary.addItemReceived(randomReward, rewardCount);
            response.addItemReceived(randomReward, rewardCount);
            profile.updateBackpack(randomReward.getId(), rewardCount);
        }

        // Quests?
        // Achievements?
        // Special events?

        return response;
    }

    private Item[] generateItemArray(Map<Item, ItemDrop> possibleItems) {
        List<Item> items = new ArrayList<>();

        for (Map.Entry<Item, ItemDrop> entry : possibleItems.entrySet()) {
            Item item = entry.getKey();
            ItemDrop itemDrop = entry.getValue();
            for (int i=0;i<itemDrop.dropChance();i++) {
                items.add(item);
            }
        }

        Collections.shuffle(items);
        return items.toArray(Item[]::new);
    }
}