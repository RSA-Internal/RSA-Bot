package org.rsa.adventure;

import org.rsa.adventure.model.*;
import org.rsa.entity.BaseEntity;
import org.rsa.entity.EntityManager;
import org.rsa.entity.adventure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class AdventureEntities {

    private static final Logger logger = LoggerFactory.getLogger(AdventureEntities.class);
    public static EntityManager<ActivityEntity> activityManager = new EntityManager<>(ActivityEntity.class);
    public static EntityManager<ItemEntity> itemManager = new EntityManager<>(ItemEntity.class);
    public static EntityManager<RarityEntity> rarityManager = new EntityManager<>(RarityEntity.class);
    public static EntityManager<SkillEntity> skillManager = new EntityManager<>(SkillEntity.class);
    public static EntityManager<ZoneEntity> zoneManager = new EntityManager<>(ZoneEntity.class);

    public static void registerEntities() {
        Arrays.stream(Activity.values()).iterator().forEachRemaining(ActivityEntity::fromEnum);
        logger.info("Registered activities: " + activityManager.getEntityList().size());

        for (Item item : Item.values()) {
            itemManager.addEntity(ItemEntity.fromEnum(item));
        }
        logger.info("Registered items: " + itemManager.getEntityList().size());

        for (Rarity rarity : Rarity.values()) {
            rarityManager.addEntity(RarityEntity.fromEnum(rarity));
        }
        logger.info("Registered rarities: " + rarityManager.getEntityList().size());

        for (Skill skill : Skill.values()) {
            skillManager.addEntity(SkillEntity.fromEnum(skill));
        }
        logger.info("Registered skills: " + skillManager.getEntityList().size());

        for (Zone zone : Zone.values()) {
            zoneManager.addEntity(ZoneEntity.fromEnum(zone));
        }
        logger.info("Registered zones: " + zoneManager.getEntityList().size());

        logger.info("Total activities: " + activityManager.getEntityList().size());
        logger.info("Total items: " + itemManager.getEntityList().size());
        logger.info("Total rarities: " + rarityManager.getEntityList().size());
        logger.info("Total skills: " + skillManager.getEntityList().size());
        logger.info("Total zones: " + zoneManager.getEntityList().size());
    }

    public static EntityManager<? extends BaseEntity> getEntityManagerFromType(String type) {
        return switch (type) {
            case "activity" -> activityManager;
            case "item" -> itemManager;
            case "rarity" -> rarityManager;
            case "skill" -> skillManager;
            case "zone" -> zoneManager;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
