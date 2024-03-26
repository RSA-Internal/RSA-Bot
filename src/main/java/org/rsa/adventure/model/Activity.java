package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.rsa.adventure.TravelSummaryManager;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Activity {
    LEAVE(0, "Leave", 0, 0,
        Collections.emptyMap(), Collections.emptyList(), 0, Collections.emptyMap()),
    HUNT(1, "Hunt Animals", 2, 5,
        Map.of(Skill.HUNTING, 0), List.of(Item.BASIC_KNIFE),
        2, Map.of(
            Item.NOTHING, new ItemDrop(1, 40),
            Item.BONE, new ItemDrop(2, 4),
            Item.ANIMAL_PELT, new ItemDrop(1, 1),
            Item.RAW_MEAT, new ItemDrop(1, 55)
        )),
    FORAGE(2, "Forage", 2, 3,
        Map.of(Skill.FORAGING, 0), List.of(),
        3, Map.of(
            Item.NOTHING, new ItemDrop(1, 40),
            Item.BERRY, new ItemDrop(3, 13),
            Item.STICK, new ItemDrop(2, 17),
            Item.PLANT_FIBER, new ItemDrop(4, 20),
            Item.ROCK, new ItemDrop(1, 10)
        )),
    FISH(3, "Fish", 2, 2,
        Map.of(Skill.FISHING, 0), List.of(Item.BASIC_FISHING_ROD),
        1, Map.of(
            Item.NOTHING, new ItemDrop(1, 40),
            Item.STICK, new ItemDrop(1, 10),
            Item.BONE, new ItemDrop(1, 1),
            Item.KELP, new ItemDrop(2, 30),
            Item.RAW_FISH, new ItemDrop(1, 19)
        )),
    RELAX(4, "Relax", 0, 0,
        Map.of(Skill.NO_SKILL, 0), Collections.emptyList(), 0, Collections.emptyMap()),
    MINE(5, "Mine", 4, 5,
        Map.of(Skill.MINING, 0), List.of(Item.BASIC_PICKAXE),
        2, Map.of(
            Item.ROCK, new ItemDrop(4, 100)
        )),
    FARM(6, "Farm", 3, 3,
        Map.of(Skill.FORAGING, 2), List.of(Item.BASIC_HOE),
        3, Map.of(
            Item.NOTHING, new ItemDrop(1, 30),
            Item.BERRY, new ItemDrop(2, 15),
            Item.CARROT, new ItemDrop(4, 25),
            Item.POTATO, new ItemDrop(3, 15),
            Item.PLANT_FIBER, new ItemDrop(2, 6),
            Item.ROCK, new ItemDrop(2, 9)
        )),
    CHOP(7, "Chop Tree", 4, 4,
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

    public static List<SelectOption> getActivityOptionList() {
        return activityStream()
            .filter(activity -> activity.id > 0)
            .map(activity ->
                SelectOption
                    .of(activity.name, "activity-" + activity.id)
                    .withDescription("")
                    .withDefault(activity.id == 1))
            .toList();
    }

    public static String getPossibleItemsAsString(Activity activity, boolean depth, boolean includeNothing) {
        return activity.possibleItems
            .keySet().stream()
            .filter(item -> {
                if (Item.NOTHING.getId().equals(item.getId())) {
                    return includeNothing;
                }
                return true;
            })
            .map(item -> {
                ItemDrop itemDrop = activity.possibleItems.get(item);
                StringBuilder range = new StringBuilder();
                if (depth) {
                    range.append(" ");
                }
                range.append("- ");
                if (!Item.NOTHING.getId().equals(item.getId())) {
                    range.append("1");
                    if (itemDrop.dropMax() > 1) {
                        range.append(" - ");
                        range.append(itemDrop.dropMax());
                    }
                    range.append(" ");
                }
                range.append(item.getName());
                range.append(" (");
                range.append(itemDrop.dropChance());
                range.append("%)");

                return range.toString();
            })
            .collect(Collectors.joining("\n"));
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
                System.out.println("Skill " + skill.getName() + " leveled up. Checking for new zones.");
                List<Zone> unlockedZones = skill.unlockZonesOnLevelUp(profile);
                System.out.println("Unlocked new zones: " + unlockedZones.size());
                for (Zone zone : unlockedZones) {
                    System.out.println("Unlocked zone: " + zone.getName());
                    travelSummary.addUnlockedZone(zone);
                    response.addUnlockedZone(zone);
                }
            }
        }

        // Item roll
        Item[] items = generateItemArray(possibleItems);
        int rolls = random.nextInt(1, rewardRolls);

        for (int i=0;i<rolls;i++) {
            Item randomReward = items[random.nextInt(items.length)];
            int rewardCount = 1;
            if (!Item.NOTHING.equals(randomReward)) {
                ItemDrop itemDropForReward = possibleItems.get(randomReward);
                if (itemDropForReward.dropMax() > 1) {
                    rewardCount = random.nextInt(1, itemDropForReward.dropMax());
                }
                travelSummary.addItemReceived(randomReward, rewardCount);
                response.addItemReceived(randomReward, rewardCount);
                profile.updateBackpack(randomReward.getId(), rewardCount);
            }
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

    public String getAsDetails() {
        /**
         * private final Integer id;
         *     private final String name;
         *     private final Integer staminaRequirement;
         *     private final Integer experienceGainBound;
         *     private final Map<Skill, Integer> requiredSkillSet;
         *     private final List<Item> requiredItems;
         *     private final Integer rewardRolls;
         *     private final Map<Item, ItemDrop> possibleItems;
         */
        StringBuilder builder = new StringBuilder();
        builder.append("- ID: ");
        builder.append(id);
        builder.append("\n- Name: ");
        builder.append(name);
        builder.append("\n- Required Stamina: ");
        builder.append(staminaRequirement);
        builder.append("\n- Experience Range: ");
        builder.append("1");
        if (experienceGainBound > 1) {
            builder.append(" - ");
            builder.append(experienceGainBound);
        }
        if (!requiredSkillSet.keySet().isEmpty()) {
            builder.append("\n- Required Skills");
            for (Skill skill : requiredSkillSet.keySet()) {
                builder.append("\n - ");
                builder.append(skill.getName());
                int requiredLevel = requiredSkillSet.get(skill);
                if (requiredLevel > 0) {
                    builder.append(" [Level: ");
                    builder.append(requiredSkillSet.get(skill));
                    builder.append("]");
                }
            }
        }
        if (!requiredItems.isEmpty()) {
            builder.append("\n- Required Items");
            for (Item item : requiredItems) {
                builder.append("\n - ");
                builder.append(item.getName());
            }
        }
        builder.append("\n- Reward rolls per action: ");
        builder.append("1");
        if (rewardRolls > 1) {
            builder.append(" - ");
            builder.append(rewardRolls);
        }
        builder.append("\n- Possible Rewards\n");
        builder.append(getPossibleItemsAsString(this, true, true));

        return builder.toString();
    }
}