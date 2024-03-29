package org.rsa.entity.adventure;

import lombok.Getter;
import org.rsa.entity.BaseEntity;
import org.rsa.entity.loot.LootTable;
import org.rsa.logic.data.models.UserAdventureProfile;
import org.rsa.manager.adventure.CooldownManager;
import org.rsa.manager.adventure.TravelSummaryManager;
import org.rsa.model.adventure.entity.Activity;
import org.rsa.model.adventure.entity.Item;
import org.rsa.model.adventure.entity.Skill;
import org.rsa.model.adventure.loot.LootTableEntry;
import org.rsa.model.adventure.response.ActivityPerformResponse;
import org.rsa.model.adventure.response.ActivityResponse;
import org.rsa.register.adventure.EntityManagerRegister;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
public class ActivityEntity extends BaseEntity {

    private final Integer experienceGainBound;
    private final Integer rewardRolls;
    private final List<ItemEntity> requiredItems;
    private final List<LootTable> lootTables;
    private final List<SkillEntity> requiredSkillSet;

    public static ActivityEntity fromEnum(Activity activity) {
        List<SkillEntity> requiredSkills = new ArrayList<>();
        for (Map.Entry<Skill, Integer> enumSkills : activity.getRequiredSkillSet().entrySet()) {
            SkillEntity newEntity = SkillEntity.fromEnum(enumSkills.getKey());
            newEntity.setLevel(enumSkills.getValue());
            requiredSkills.add(newEntity);
        }

        return new ActivityEntity(
            activity.getId(),
            activity.getName(),
            activity.getExperienceGainBound(),
            activity.getRewardRolls(),
            activity.getRequiredItems().stream().map(ItemEntity::fromEnum).toList(),
            activity.getLootTables(),
            requiredSkills);
    }

    public ActivityEntity(Integer id,
                          String name,
                          Integer experienceGainBound,
                          Integer rewardRolls,
                          List<ItemEntity> requiredItems,
                          List<LootTable> lootTables,
                          List<SkillEntity> requiredSkillSet) {
        super(id, name);
        this.experienceGainBound = experienceGainBound;
        this.rewardRolls = rewardRolls;
        this.requiredItems = requiredItems;
        this.lootTables = lootTables;
        this.requiredSkillSet = requiredSkillSet;
        EntityManagerRegister.activityManager.addEntity(this);
    }

    @Override
    public String getAsDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append("- ID: ");
        builder.append(getId());
        builder.append("\n- Name: ");
        builder.append(getName());
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

    public ActivityResponse userCanPerformActivity(UserAdventureProfile profile, boolean ignoreCooldown) {
        if (!ignoreCooldown) {
            long isCooldownReady = CooldownManager.isCooldownReady(profile.getUserid());
            if (isCooldownReady > 0) {
                return new ActivityResponse(false, String.join(" ", "You must wait " + (isCooldownReady / 1000.0) + "s before performing this action."));
            }
        }

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
        for (LootTable table : getLootTables()) {
            int rolls = 1;
            if (rewardRolls > 1) {
                rolls = random.nextInt(1, rewardRolls);
            }

            for (int i=0;i<rolls;i++) {
                LootTableEntry randomReward = table.getDrop();
                ItemEntity item = randomReward.getItemToDrop();

                if (!Item.NOTHING.getId().equals(item.getId())) {
                    int rewardCount = randomReward.getMinDrop();
                    int max = randomReward.getMaxDrop();

                    if (max > rewardCount) {
                        rewardCount = random.nextInt(rewardCount, max);
                    }

                    travelSummary.addItemReceived(item, rewardCount);
                    response.addItemReceived(item, rewardCount);
                    profile.updateBackpack(item.getId(), rewardCount);
                }
            }
        }

        // Quests?
        // Achievements?
        // Special events?
        TravelSummaryManager.updateTravelSummary(profile.getUserid(), travelSummary);
        CooldownManager.setUserCooldown(profile.getUserid(), System.currentTimeMillis());

        return response;
    }

    public static String getPossibleItemsAsString(ActivityEntity activity, boolean depth, boolean includeNothing) {
        List<LootTable> lootTables = activity.getLootTables();
        AtomicInteger index = new AtomicInteger();

        return activity.lootTables.stream()
            .map(table -> {
                String tableRender = table.generateDisplay(depth, includeNothing);
                if (lootTables.size() > 1) {
                    String display = "Table " + (index.get() + 1) + "\n" + tableRender;
                    index.getAndIncrement();
                    return display;
                } else {
                    return tableRender;
                }
            })
            .collect(Collectors.joining("\n"));
    }
}
