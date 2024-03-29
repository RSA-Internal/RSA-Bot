package org.rsa.helper;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.rsa.manager.adventure.IndexManager;

import java.util.ArrayList;
import java.util.List;

import static org.rsa.manager.adventure.IndexManager.*;

public class AdventureIndexHelper {

    public static int getFirstEntityIdInList(ActionRow entityActionRow) {
        ItemComponent firstComponent = entityActionRow.getComponents().get(0);
        SelectOption firstOption = ((StringSelectMenu) firstComponent).getOptions().get(0);
        String firstEntityValue = firstOption.getValue();
        int firstHyphenIndex = firstEntityValue.indexOf("-");
        int secondHyphenIndex = firstEntityValue.indexOf("-", firstHyphenIndex + 1);
        return Integer.parseInt(firstEntityValue.substring(firstEntityValue.indexOf("-") + 1, secondHyphenIndex));
    }

    public static List<ActionRow> getActionRowsForResponse(Member requester) {
        int selectedEntityId = IndexManager.getUserSelectedEntityId(requester.getId());
        List<ActionRow> actionRows = getActionRows(requester);
        ActionRow entityActionRow = ActionRow.of(getIndexSelectEntity(requester));
        actionRows.add(entityActionRow);

        if (selectedEntityId == -1) {
            int firstEntityId = getFirstEntityIdInList(entityActionRow);
            IndexManager.setUserSelectedEntityId(requester.getId(), firstEntityId);
        }

        return actionRows;
    }

    private static List<ActionRow> getActionRows(Member requester) {
        List<ActionRow> actionRows = new ArrayList<>();
        actionRows.add(ActionRow.of(getIndexSelectType(requester)));

        int pageCount = getPageCountForUser(requester.getId());
        List<SelectOption> pageList = new ArrayList<>();

        int getSelectedPage = IndexManager.getUserPage(requester.getId());
        int defaultIndex = Math.max(getSelectedPage, 0);

        for (int i=0;i<pageCount;i++) {
            // Page list show 1 - max || current - max
            // Bonus: Show 1, selected - max, end (ie: 1, 2-23, max)
            pageList.add(
                SelectOption
                    .of("Page " + (i + 1) + " (" + getLetterRangeForPage(requester, i) + ")", "page-" + i)
                    .withDefault(i == defaultIndex)
            );
        }

        actionRows.add(ActionRow.of(
            StringSelectMenu
                .create("index-select-page")
                .addOptions(pageList)
                .setDisabled(pageCount == 1)
                .setMaxValues(1)
                .build()
        ));

        return actionRows;
    }
}
