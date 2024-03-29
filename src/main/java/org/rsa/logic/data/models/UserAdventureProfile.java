package org.rsa.logic.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.rsa.register.adventure.EntityManagerRegister;
import org.rsa.model.adventure.entity.Activity;
import org.rsa.model.adventure.entity.Skill;
import org.rsa.model.adventure.entity.Zone;
import org.rsa.entity.EntityManager;
import org.rsa.entity.adventure.ActivityEntity;
import org.rsa.entity.adventure.ItemEntity;
import org.rsa.entity.adventure.SkillEntity;
import org.rsa.entity.adventure.ZoneEntity;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAdventureProfile {
    Integer schema_version = 1;

    String guildid;
    String userid;

    /* Wallet stored as: currency - total */
    Map<Integer, BigInteger> wallet;

    /* Backpack stored as: itemId - count */
    Map<Integer, BigInteger> backpack;

    /* Equipment stored as: slotId - itemId */
    Map<Integer, Integer> equipment;

    List<Integer> unlockedAchievements;
    List<Integer> unlockedZones;

    Map<Integer, Integer> skillSetLevel;
    Map<Integer, Integer> skillSetExperience;
    Map<Integer, BigInteger> activitiesPerformed;

    String colorCode;

    public UserAdventureProfile(String guildId, String userId) {
        this.guildid = guildId;
        this.userid = userId;
        wallet = new HashMap<>();
        backpack = new HashMap<>();
        equipment = new HashMap<>();
        unlockedAchievements = new ArrayList<>();
        unlockedZones = new ArrayList<>() {{
            add(Zone.START_TOWN.getId());
            add(Zone.FOREST.getId());
            add(Zone.RIVER.getId());
            add(Zone.CAVE.getId());
        }};
        skillSetLevel = new HashMap<>();
        skillSetExperience = new HashMap<>();

        for (Skill skill : Skill.values()) {
            skillSetLevel.put(skill.getId(), 0);
            skillSetExperience.put(skill.getId(), 0);
        }

        activitiesPerformed = new HashMap<>();

        for (Activity activity : Activity.values()) {
            activitiesPerformed.put(activity.getId(), BigInteger.ZERO);
        }
    }

    public boolean validateProfile() {
        boolean validated = false;

        if (wallet == null) {
            wallet = new HashMap<>();
            validated = true;
        }
        if (backpack == null) {
            backpack = new HashMap<>();
            validated = true;
        }
        if (equipment == null) {
            equipment = new HashMap<>();
            validated = true;
        }
        if (unlockedAchievements == null) {
            unlockedAchievements = new ArrayList<>();
            validated = true;
        }
        if (unlockedZones == null) {
            unlockedZones = new ArrayList<>();
            validated = true;
        }
        if (skillSetLevel == null) {
            skillSetLevel = new HashMap<>();
            validated = true;
        }
        if (skillSetExperience == null) {
            skillSetExperience = new HashMap<>();
            validated = true;
        }

        for (Skill skill : Skill.values()) {
            if (!skillSetLevel.containsKey(skill.getId())) {
                skillSetLevel.put(skill.getId(), 0);
                validated = true;
            }
            if (!skillSetExperience.containsKey(skill.getId())) {
                skillSetExperience.put(skill.getId(), 0);
                validated = true;
            }
        }

        if (activitiesPerformed == null) {
            activitiesPerformed = new HashMap<>();
            validated = true;
        }

        return validated;
    }

    @DynamoDbSortKey
    public String getUserid() {
        return userid;
    }

    @DynamoDbPartitionKey
    public String getGuildid() {
        return guildid;
    }

    private BigInteger getNewValue(Map<Integer, BigInteger> container, int key, int change) {
        BigInteger oldValue = container.getOrDefault(key, BigInteger.ZERO);
        return oldValue.add(BigInteger.valueOf(change));
    }

    /**
     * Updates the provide container with the value for key.
     * @param container Container to modify.
     * @param key Key to update.
     * @param value New value for key.
     * @return true if successfully updated, false is the newValue is < 0.
     */
    private boolean updateContainer(Map<Integer, BigInteger> container, int key, BigInteger value) {
        // signum as -1 indicates a negative value of BigInteger.
        // We should deny any update that causes negative money.
        if (value.signum() == -1) {
            return false;
        }
        container.put(key, value);
        return true;
    }

    /**
     * Initiates a transaction against the User's wallet.
     * @param currency Currency key.
     * @param change The value of the transaction.
     * @return true if the transaction was successful.
     */
    public boolean updateWallet(Integer currency, int change) {
        BigInteger newValue = getNewValue(wallet, currency, change);
        return updateContainer(wallet, currency, newValue);
    }

    /**
     * Initiates a transaction against the User's backpack.
     * @param itemId Item key.
     * @param change The value of the transaction.
     * @return true if the transaction was successful.
     */
    public boolean updateBackpack(Integer itemId, int change) {
        BigInteger newValue = getNewValue(backpack, itemId, change);
        return updateContainer(backpack, itemId, newValue);
    }

    /**
     * Increment the times an activity has been performed by 1.
     * @param activity The activity performed.
     */
    public void performActivity(ActivityEntity activity) {
        BigInteger newValue = getNewValue(activitiesPerformed, activity.getId(), 1);
        updateContainer(activitiesPerformed, activity.getId(), newValue);
    }

    public boolean updateSkillSet(SkillEntity skill, int experience) {
        int currentLevel = skillSetLevel.get(skill.getId());
        int currentExperience = skillSetExperience.get(skill.getId());
        int requiredExperience = SkillEntity.getRequiredExperienceForLevelUp(skill, currentLevel);
        boolean leveledUp = false;
        currentExperience += experience;
        if (currentExperience >= requiredExperience) {
            currentLevel += 1;
            currentExperience -= requiredExperience;
            leveledUp = true;
        }
        skillSetLevel.put(skill.getId(), currentLevel);
        skillSetExperience.put(skill.getId(), currentExperience);

        return leveledUp;
    }

    public String getWalletAsString() {
        return "Wallet is empty.";
    }

    public String getBackpackAsString() {
        StringBuilder builder = new StringBuilder();
        EntityManager<ItemEntity> entityManager = EntityManagerRegister.itemManager;

        for (Map.Entry<Integer, BigInteger> itemEntry : backpack.entrySet()) {
            ItemEntity item = entityManager.getEntityById(itemEntry.getKey());
            String rarityDisplay = "[" + item.getRarity().getPrefix() + "] ";
            builder.append(rarityDisplay);
            builder.append(item.getName());
            builder.append(": ");
            builder.append(itemEntry.getValue().toString());
            builder.append("\n");
        }

        return builder.isEmpty() ? "Backpack is empty." : String.join("", builder);
    }

    public int getBackpackValue() {
        EntityManager<ItemEntity> entityManager = EntityManagerRegister.itemManager;
        int value = 0;

        for (Map.Entry<Integer, BigInteger> itemEntry : backpack.entrySet()) {
            ItemEntity item = entityManager.getEntityById(itemEntry.getKey());
            int count = itemEntry.getValue().intValue();
            int valuationPerItem = item.getSellPrice();
            int totalValuation = count * valuationPerItem;
            value += totalValuation;
        }

        return value;
    }

    public String getZonesAsString() {
        StringBuilder builder = new StringBuilder();

        EntityManager<ZoneEntity> entityManager = EntityManagerRegister.zoneManager;
        for (Integer unlockedZoneId : unlockedZones) {
            ZoneEntity unlockedZone = entityManager.getEntityById(unlockedZoneId);
            if (unlockedZone != null) {
                builder.append(unlockedZone.getName());
                builder.append("\n");
            }
        }

        return builder.isEmpty() ? "No unlocked zones." : builder.toString();
    }

    public String getSkillsAsString() {
        StringBuilder builder = new StringBuilder();

        List<SkillEntity> skills = EntityManagerRegister.skillManager.getEntityList();

        for (SkillEntity skill : skills) {
            if (Skill.NO_SKILL.getId().equals(skill.getId())) continue;
            int level = skillSetLevel.getOrDefault(skill.getId(), 0);
            int exp = skillSetExperience.getOrDefault(skill.getId(), 0);
            int reqExp = SkillEntity.getRequiredExperienceForLevelUp(skill, level + 1);

            builder.append(skill.getName());
            builder.append(": Level ");
            builder.append(level);
            builder.append(" (");
            builder.append(exp);
            builder.append(" / ");
            builder.append(reqExp);
            builder.append(" xp)\n");
        }

        return builder.isEmpty() ? "No skill data." : builder.toString();
    }
}
