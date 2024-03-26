package org.rsa.adventure;

import org.rsa.adventure.model.*;
import org.rsa.entity.BaseEntity;
import org.rsa.entity.EntityManager;
import org.rsa.entity.adventure.*;

public class AdventureEntities {

    public static EntityManager<ActivityEntity> activityManager = new EntityManager<>(ActivityEntity.class);
    public static EntityManager<ItemEntity> itemManager = new EntityManager<>(ItemEntity.class);
    public static EntityManager<RarityEntity> rarityManager = new EntityManager<>(RarityEntity.class);
    public static EntityManager<SkillEntity> skillManager = new EntityManager<>(SkillEntity.class);
    public static EntityManager<ZoneEntity> zoneManager = new EntityManager<>(ZoneEntity.class);

    public static void registerEntities() throws Exception {
        for (Activity activity : Activity.values()) {
            activityManager.addEntity(ActivityEntity.fromEnum(activity));
        }

        for (Item item : Item.values()) {
            itemManager.addEntity(ItemEntity.fromEnum(item));
        }

        for (Rarity rarity : Rarity.values()) {
            rarityManager.addEntity(RarityEntity.fromEnum(rarity));
        }

        for (Skill skill : Skill.values()) {
            skillManager.addEntity(SkillEntity.fromEnum(skill));
        }

        for (Zone zone : Zone.values()) {
            zoneManager.addEntity(ZoneEntity.fromEnum(zone));
        }
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
