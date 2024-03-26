package org.rsa.util;

import org.rsa.adventure.model.*;

public class EntityStringUtil {

    public static String getEntityName(String selectedEntity) {
        String selectedType = selectedEntity.substring(0, selectedEntity.indexOf("-"));
        String selectedIndex = selectedEntity.substring(selectedEntity.indexOf("-") + 1);
        int index = Integer.parseInt(selectedIndex);

        return switch (selectedType) {
            case "activity" -> getEntityName(Activity.getById(index));
            case "item" -> getEntityName(Item.getById(index));
            case "rarity" -> getEntityName(Rarity.getById(index));
            case "skill" -> getEntityName(Skill.getById(index));
            case "zone" -> getEntityName(Zone.getById(index));
            default -> "Invalid Entity";
        };
    }

    public static String getEntityName(Activity entity) {
        return entity.getName();
    }

    public static String getEntityName(Item entity) {
        return entity.getName();
    }

    public static String getEntityName(Rarity entity) {
        return entity.getName();
    }

    public static String getEntityName(Skill entity) {
        return entity.getName();
    }

    public static String getEntityName(Zone entity) {
        return entity.getName();
    }

    public static String getEntityDetails(String selectedEntity) {
        String selectedType = selectedEntity.substring(0, selectedEntity.indexOf("-"));
        String selectedIndex = selectedEntity.substring(selectedEntity.indexOf("-") + 1);
        int index = Integer.parseInt(selectedIndex);

        return switch (selectedType) {
            case "activity" -> getEntityDetails(Activity.getById(index));
            case "item" -> getEntityDetails(Item.getById(index));
            case "rarity" -> getEntityDetails(Rarity.getById(index));
            case "skill" -> getEntityDetails(Skill.getById(index));
            case "zone" -> getEntityDetails(Zone.getById(index));
            default -> "Invalid Entity";
        };
    }

    public static String getEntityDetails(Activity entity) {
        return entity.getAsDetails();
    }

    public static String getEntityDetails(Item entity) {
        return entity.getAsDetails();
    }

    public static String getEntityDetails(Rarity entity) {
        return entity.getAsDetails();
    }

    public static String getEntityDetails(Skill entity) {
        return entity.getAsDetails();
    }

    public static String getEntityDetails(Zone entity) {
        return entity.getAsDetails();
    }
}
