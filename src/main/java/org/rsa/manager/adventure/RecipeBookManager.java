package org.rsa.manager.adventure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.rsa.entity.EntityManager;
import org.rsa.entity.recipe.RecipeEntity;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;
import org.rsa.register.adventure.EntityManagerRegister;
import org.rsa.util.EmbedBuilderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeBookManager {

    private static final Map<String, Integer> userPage = new HashMap<>();
    private static final Map<String, Integer> userRecipeIndex = new HashMap<>();
    private static final Map<String, Integer> userRecipeId = new HashMap<>();
    private static final Map<String, String> userFilter = new HashMap<>();

    public static MessageEmbed generateRecipeBook(Guild guild, Member requester) {
        return EmbedBuilderUtil.getEmbedBuilderTemplate(guild, requester, "Recipe Book")
            .addField("Test Recipe", "\u200b", false)
            .addField("Inputs", "- 1x Nothing", false)
            .addField("Outputs", "- 1x Nothing", false)
            .build();
    }

    private static StringSelectMenu generatePageSelectMenu(Member requester) {
        EntityManager<RecipeEntity> recipeManager = EntityManagerRegister.recipeManager;
        String filter = getUserFilter(requester.getId());
        int pageCount = recipeManager.getPageCount(filter);

        StringSelectMenu.Builder builder = StringSelectMenu.create("recipe-select-page");

        for (int i=0;i<pageCount;i++) {
            builder.addOptions(
                SelectOption
                    .of("Page " + (i+1) + " (" + recipeManager.getLetterRangeForPage(i, filter) + ")", "page-" + (i+1))
                    .withDefault(i == 0));
        }

        builder.setDisabled(pageCount == 1);
        builder.setMaxValues(1);

        return builder.build();
    }

    private static StringSelectMenu generateRecipeList(Member requester, int page) {
        EntityManager<RecipeEntity> recipeManager = EntityManagerRegister.recipeManager;
        String filter = getUserFilter(requester.getId());
        List<SelectOption> options = recipeManager.getOptionList(getUserRecipeIndex(requester.getId()), page, filter);

        return StringSelectMenu.create("recipe-select-recipe").addOptions(options).setMaxValues(1).build();
    }

    public static List<ActionRow> generateActionRows(Member requester) {
        List<ActionRow> actionRows = new ArrayList<>();
        int recipeId = getUserRecipeId(requester.getId());
        RecipeEntity recipe = EntityManagerRegister.recipeManager.getEntityById(recipeId);
        int userPage = getUserPage(requester.getId());

        UserAdventureProfile profile = UserAdventureProfileManager.fetch(requester.getGuild().getId(), requester.getId());

        actionRows.add(ActionRow.of(Button.primary("craft_" + recipeId, "Craft " + recipe.getName()).withDisabled(!recipe.canCraft(profile))));
        actionRows.add(ActionRow.of(generatePageSelectMenu(requester)));
        actionRows.add(ActionRow.of(generateRecipeList(requester, userPage)));
        return actionRows;
    }

    public static void setUserPage(String userId, int page) {
        userPage.put(userId, page);
    }

    public static int getUserPage(String userId) {
        return userPage.get(userId);
    }

    public static void setUserRecipeIndex(String userId, int recipeIndex) {
        userRecipeIndex.put(userId, recipeIndex);
    }

    public static int getUserRecipeIndex(String userId) {
        return userRecipeIndex.get(userId);
    }

    public static void setUserRecipeId(String userId, int recipeId) {
        userRecipeId.put(userId, recipeId);
    }

    public static int getUserRecipeId(String userId) {
        return userRecipeId.get(userId);
    }

    public static void setUserFilter(String userId, String filter) {
        userFilter.put(userId, filter);
    }

    public static String getUserFilter(String userId) {
        return userFilter.get(userId);
    }
}
