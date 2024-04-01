package org.rsa.register.adventure;

import org.rsa.entity.BaseEntity;
import org.rsa.entity.EntityManager;
import org.rsa.entity.adventure.*;
import org.rsa.entity.recipe.RecipeEntity;
import org.rsa.model.adventure.Currency;
import org.rsa.model.adventure.entity.*;
import org.rsa.model.adventure.recipe.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class EntityManagerRegister {

    private static final Logger logger = LoggerFactory.getLogger(EntityManagerRegister.class);
    public static EntityManager<ActivityEntity> activityManager = new EntityManager<>();
    public static EntityManager<ItemEntity> itemManager = new EntityManager<>();
    public static EntityManager<RarityEntity> rarityManager = new EntityManager<>();
    public static EntityManager<SkillEntity> skillManager = new EntityManager<>();
    public static EntityManager<ZoneEntity> zoneManager = new EntityManager<>();
    public static EntityManager<RecipeEntity> recipeManager = new EntityManager<>();
    public static EntityManager<CurrencyEntity> currencyManager = new EntityManager<>();

    public static void registerEntities() {
        Arrays.stream(Activity.values()).iterator().forEachRemaining(ActivityEntity::fromEnum);
        logger.info("Registered activities: " + activityManager.getEntityList().size());

        Arrays.stream(Item.values()).iterator().forEachRemaining(ItemEntity::fromEnum);
        logger.info("Registered items: " + itemManager.getEntityList().size());

        Arrays.stream(Rarity.values()).iterator().forEachRemaining(RarityEntity::fromEnum);
        logger.info("Registered rarities: " + rarityManager.getEntityList().size());

        Arrays.stream(Skill.values()).iterator().forEachRemaining(SkillEntity::fromEnum);
        logger.info("Registered skills: " + skillManager.getEntityList().size());

        Arrays.stream(Zone.values()).iterator().forEachRemaining(ZoneEntity::fromEnum);
        logger.info("Registered zones: " + zoneManager.getEntityList().size());

        Arrays.stream(Recipe.values()).iterator().forEachRemaining(RecipeEntity::fromEnum);
        logger.info("Registered recipes: " + recipeManager.getEntityList().size());

        Arrays.stream(Currency.values()).iterator().forEachRemaining(CurrencyEntity::fromEnum);
        logger.info("Registered currencies: " + currencyManager.getEntityList().size());

        logger.info("Total activities: " + activityManager.getEntityList().size());
        logger.info("Total items: " + itemManager.getEntityList().size());
        logger.info("Total rarities: " + rarityManager.getEntityList().size());
        logger.info("Total skills: " + skillManager.getEntityList().size());
        logger.info("Total zones: " + zoneManager.getEntityList().size());
        logger.info("Total recipes: " + recipeManager.getEntityList().size());
        logger.info("Total currencies: " + currencyManager.getEntityList().size());
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
