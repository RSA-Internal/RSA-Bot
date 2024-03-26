package org.rsa.adventure;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.rsa.entity.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexManager {

    private static final Logger logger = LoggerFactory.getLogger(IndexManager.class);

    private static final Map<String, String> userToTypeMap = new HashMap<>();

    public static void setUserTypeSelection(String userId, String typeSelection) {
        userToTypeMap.put(userId, typeSelection);
    }

    public static String getUserTypeSelection(String userId) {
        return userToTypeMap.get(userId);
    }

    public static List<SelectOption> getOptionsForUser(String userId, int defaultValue) {
        String typeSelection = getUserTypeSelection(userId);
        EntityManager<?> entityManager = AdventureEntities.getEntityManagerFromType(typeSelection);
        return entityManager != null ? entityManager.getOptionList(defaultValue) : Collections.emptyList();
    }



    public static StringSelectMenu getIndexSelectType(Member requester) {
        String selectedType = getUserTypeSelection(requester.getId());

        return StringSelectMenu
            .create("index-select-type")
            .addOptions(
                SelectOption.of("Activities", "index-activity").withDefault(selectedType.equals("activity")),
                SelectOption.of("Items", "index-item").withDefault(selectedType.equals("item")),
                SelectOption.of("Rarities", "index-rarity").withDefault(selectedType.equals("rarity")),
                SelectOption.of("Skills", "index-skill").withDefault(selectedType.equals("skill")),
                SelectOption.of("Zones", "index-zone").withDefault(selectedType.equals("zone"))
            )
            .setMaxValues(1)
            .build();
    }

    public static StringSelectMenu getIndexSelectEntity(Member requester) {
        return getIndexSelectEntity(requester, 1);
    }

    public static StringSelectMenu getIndexSelectEntity(Member requester, String selectedEntity) {
        String selectedEntityIndex = selectedEntity.substring(selectedEntity.indexOf("-") + 1);
        return getIndexSelectEntity(requester, Integer.parseInt(selectedEntityIndex));
    }

    public static StringSelectMenu getIndexSelectEntity(Member requester, int defaultValue) {
        return StringSelectMenu
            .create("index-select-entity")
            .addOptions(getOptionsForUser(requester.getId(), defaultValue))
            .setMaxValues(1)
            .build();
    }
}
