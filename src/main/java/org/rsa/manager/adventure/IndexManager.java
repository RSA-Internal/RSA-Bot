package org.rsa.manager.adventure;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.rsa.register.adventure.EntityManagerRegister;
import org.rsa.entity.BaseEntity;
import org.rsa.entity.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public class IndexManager {

    private static final Logger logger = LoggerFactory.getLogger(IndexManager.class);

    private static final Map<String, String> userToTypeMap = new HashMap<>();
    private static final Map<String, Integer> userPage = new HashMap<>();
    private static final Map<String, Integer> userSelectedEntityIndex = new HashMap<>();
    private static final Map<String, Integer> userSelectedEntityId = new HashMap<>();

    public static void setUserTypeSelection(String userId, String typeSelection) {
        logger.info("Setting type for {} to {}", userId, typeSelection);
        userToTypeMap.put(userId, typeSelection);
        setUserPage(userId, 0);
        setUserSelectedEntityIndex(userId, 0);
        setUserSelectedEntityId(userId, 1);
    }

    public static String getUserTypeSelection(String userId) {
        return userToTypeMap.get(userId);
    }

    public static void setUserPage(String userId, int page) {
        logger.info("Setting page for {} to {}", userId, page);
        userPage.put(userId, page);
    }

    public static int getUserPage(String userId) {
        return userPage.get(userId);
    }

    public static void setUserSelectedEntityIndex(String userId, int entityIndex) {
        logger.info("Setting index for {} to {}", userId, entityIndex);
        userSelectedEntityIndex.put(userId, entityIndex);
    }

    public static int getUserSelectedEntityIndex(String userId) {
        return userSelectedEntityIndex.get(userId);
    }

    public static void setUserSelectedEntityId(String userId, int entityId) {
        logger.info("Setting id for {} to {}", userId, entityId);
        userSelectedEntityId.put(userId, entityId);
    }

    public static int getUserSelectedEntityId(String userId) {
        return userSelectedEntityId.get(userId);
    }

    public static List<SelectOption> getOptionsForUser(String userId, int defaultValue) {
        logger.info("getOptionsForUser - userId: {} - defaultValue: {}", userId, defaultValue);
        String typeSelection = getUserTypeSelection(userId);
        EntityManager<?> entityManager = EntityManagerRegister.getEntityManagerFromType(typeSelection);
        int currentPage = getUserPage(userId);

        if (defaultValue == -1) {
//            BaseEntity firstEntity = entityManager.getPaginatedEntities(IndexManager.getUserPage(userId), Comparator.comparing(BaseEntity::getName)).findFirst().orElse(null);
//            if (firstEntity != null) {
//                defaultValue = firstEntity.getId();
//                IndexManager.setUserSelectedEntityId(userId, defaultValue);
//            }
            defaultValue = 0;
        }

        return entityManager != null ? entityManager.getOptionList(defaultValue, currentPage) : Collections.emptyList();
    }

    public static int getPageCountForUser(String userId) {
        String typeSelection = getUserTypeSelection(userId);
        EntityManager<?> entityManager = EntityManagerRegister.getEntityManagerFromType(typeSelection);
        int remaining = entityManager.getEntityList().size() % 25;
        int pageCount = entityManager.getEntityList().size() / 25;
        if (remaining > 0) {
            pageCount += 1;
        }
        return pageCount;
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
        return getIndexSelectEntity(requester, getUserSelectedEntityIndex(requester.getId()));
    }

    public static StringSelectMenu getIndexSelectEntity(Member requester, int defaultValue) {
        logger.info("getIndexSelectEntity - requester: {} - defaultValue: {}", requester.getId(), defaultValue);
        return StringSelectMenu
            .create("index-select-entity")
            .addOptions(getOptionsForUser(requester.getId(), defaultValue))
            .setMaxValues(1)
            .build();
    }

    public static String getLetterRangeForPage(Member requester, int page) {
        String typeSelection = getUserTypeSelection(requester.getId());
        EntityManager<?> entityManager = EntityManagerRegister.getEntityManagerFromType(typeSelection);
        Stream<? extends BaseEntity> entities = entityManager.getPaginatedEntities(page, Comparator.comparing(BaseEntity::getName));
        List<String> names = entities.map(BaseEntity::getName).toList();
        String firstName = names.get(0);
        String lastName = names.get(names.size() - 1);

        return firstName.charAt(0) + "-" + lastName.charAt(0);
    }
}
