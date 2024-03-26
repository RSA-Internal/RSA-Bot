package org.rsa.adventure;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.rsa.adventure.model.*;
import org.rsa.util.HelperUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rsa.util.EntityStringUtil.getEntityDetails;
import static org.rsa.util.EntityStringUtil.getEntityName;

public class IndexManager {

    private static final Map<String, String> userToTypeMap = new HashMap<>();

    public static void setUserTypeSelection(String userId, String typeSelection) {
        userToTypeMap.put(userId, typeSelection);
    }

    public static String getUserTypeSelection(String userId) {
        return userToTypeMap.get(userId);
    }

    public static List<SelectOption> getOptionsForUser(String userId) {
        return getOptionsForUser(userId, 1);
    }

    public static List<SelectOption> getOptionsForUser(String userId, int defaultValue) {
        String typeSelection = getUserTypeSelection(userId);

        return switch(typeSelection) {
            case "activity" -> Activity.getActivityOptionList(defaultValue);
            case "item" -> Item.getItemOptionList(defaultValue);
            case "rarity" -> Rarity.getRarityOptionList(defaultValue);
            case "skill" -> Skill.getSkillOptionList(defaultValue);
            case "zone" -> Zone.getZoneOptionList(defaultValue);
            default -> Collections.emptyList();
        };
    }

    private static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static MessageEmbed getIndexEmbed(Member requester, String entityType, String selectedEntity) {
        return new EmbedBuilder()
            .setTitle("Index Viewer")
            .setAuthor(requester.getEffectiveName())
            .setColor(HelperUtil.getRandomColor())
            .setThumbnail("https://cdn4.iconfinder.com/data/icons/learning-31/64/dictionary_book_lexicon_work_book_thesaurus-512.png")
            .addField("Viewing: " + capitalize(entityType) + " - " + getEntityName(selectedEntity), getEntityDetails(selectedEntity), true)
            .build();
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
