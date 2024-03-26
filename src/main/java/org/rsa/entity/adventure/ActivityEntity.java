package org.rsa.entity.adventure;

import lombok.Getter;
import org.rsa.adventure.TravelSummaryManager;
import org.rsa.adventure.model.*;
import org.rsa.entity.BaseEntity;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ActivityEntity extends BaseEntity {

    private final Integer requiredStamina;
    private final Integer experienceGainBound;
    private final Integer rewardRolls;
    private final List<ItemEntity> requiredItems;
    private final List<ItemEntity> possibleItems;
    private final List<SkillEntity> requiredSkillSet;

    public static ActivityEntity fromEnum(Activity activity) {
        List<ItemEntity> possibleItems = new ArrayList<>();
        for (Map.Entry<Item, ItemDrop> enumItems : activity.getPossibleItems().entrySet()) {
            ItemEntity newEntity = ItemEntity.fromEnum(enumItems.getKey());
            newEntity.setItemDrop(enumItems.getValue());
            possibleItems.add(newEntity);
        }

        List<SkillEntity> requiredSkills = new ArrayList<>();
        for (Map.Entry<Skill, Integer> enumSkills : activity.getRequiredSkillSet().entrySet()) {
            SkillEntity newEntity = SkillEntity.fromEnum(enumSkills.getKey());
            newEntity.setLevel(enumSkills.getValue());
            requiredSkills.add(newEntity);
        }

        return new ActivityEntity(
            activity.getId(),
            activity.getName(),
            activity.getStaminaRequirement(),
            activity.getExperienceGainBound(),
            activity.getRewardRolls(),
            activity.getRequiredItems().stream().map(ItemEntity::fromEnum).toList(),
            possibleItems,
            requiredSkills);
    }

    public ActivityEntity(Integer id,
                          String name,
                          Integer requiredStamina,
                          Integer experienceGainBound,
                          Integer rewardRolls,
                          List<ItemEntity> requiredItems,
                          List<ItemEntity> possibleItems,
                          List<SkillEntity> requiredSkillSet) {
        super(id, name);
        this.requiredStamina = requiredStamina;
        this.experienceGainBound = experienceGainBound;
        this.rewardRolls = rewardRolls;
        this.requiredItems = requiredItems;
        this.possibleItems = possibleItems;
        this.requiredSkillSet = requiredSkillSet;
    }

    @Override
    public String getAsDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append("- ID: ");
        builder.append(getId());
        builder.append("\n- Name: ");
        builder.append(getName());
        builder.append("\n- Required Stamina: ");
        builder.append(requiredStamina);
        builder.append("\n- Experience Range: ");
        builder.append("1");
        if (experienceGainBound > 1) {
            builder.append(" - ");
            builder.append(experienceGainBound);
        }
        if (!requiredSkillSet.isEmpty()) {
            builder.append("\n- Required Skills");
            for (SkillEntity skill : requiredSkillSet) {
                builder.append("\n - ");
                builder.append(skill.getName());
                int requiredLevel = skill.getLevel();
                if (requiredLevel > 0) {
                    builder.append(" [Level: ");
                    builder.append(requiredLevel);
                    builder.append("]");
                }
            }
        }
        if (!requiredItems.isEmpty()) {
            builder.append("\n- Required Items");
            for (ItemEntity item : requiredItems) {
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

    public ActivityResponse userCanPerformActivity(UserAdventureProfile profile) {
        Map<Integer, Integer> skillSetLevel = profile.getSkillSetLevel();

        for (SkillEntity skill : requiredSkillSet) {
            int skillId = skill.getId();
            int requiredLevel = skill.getLevel();
            int playerLevel = skillSetLevel.get(skillId);
            if (playerLevel < requiredLevel)
                return new ActivityResponse(false, String.join(" ", skill.getName(), String.valueOf(playerLevel), "/", String.valueOf(requiredLevel)));
        }

        for (ItemEntity item : requiredItems) {
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
        for (SkillEntity skill : requiredSkillSet) {
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
                List<ZoneEntity> unlockedZones = skill.unlockZonesOnLevelUp(profile);
                System.out.println("Unlocked new zones: " + unlockedZones.size());
                for (ZoneEntity zone : unlockedZones) {
                    System.out.println("Unlocked zone: " + zone.getName());
                    travelSummary.addUnlockedZone(zone);
                    response.addUnlockedZone(zone);
                }
            }
        }

        // Item roll
        ItemEntity[] items = generateItemArray(possibleItems);
        int rolls = random.nextInt(1, rewardRolls);

        for (int i=0;i<rolls;i++) {
            ItemEntity randomReward = items[random.nextInt(items.length)];
            int rewardCount = 1;
            if (!Item.NOTHING.getId().equals(randomReward.getId())) {
                ItemDrop itemDropForReward = randomReward.getItemDrop();
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

    private ItemEntity[] generateItemArray(List<ItemEntity> possibleItems) {
        List<ItemEntity> items = new ArrayList<>();

        for (ItemEntity item : possibleItems) {
            ItemDrop itemDrop = item.getItemDrop();
            if (itemDrop == null) continue;
            for (int i=0;i<itemDrop.dropChance();i++) {
                items.add(item);
            }
        }

        Collections.shuffle(items);
        return items.toArray(ItemEntity[]::new);
    }

    public static String getPossibleItemsAsString(ActivityEntity activity, boolean depth, boolean includeNothing) {
        return activity.getPossibleItems().stream()
            .filter(item -> {
                if (Item.NOTHING.getId().equals(item.getId())) {
                    return includeNothing;
                }
                return true;
            })
            .filter(item -> item.getItemDrop() != null)
            .map(item -> {
                ItemDrop itemDrop = item.getItemDrop();
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
}
