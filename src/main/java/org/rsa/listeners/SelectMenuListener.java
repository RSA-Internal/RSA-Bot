package org.rsa.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;
import org.rsa.manager.adventure.IndexManager;
import org.rsa.manager.adventure.RecipeBookManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.rsa.helper.AdventureIndexHelper.getActionRowsForResponse;
import static org.rsa.util.EmbedBuilderUtil.getIndexEmbedBuilder;
import static org.rsa.util.EmbedBuilderUtil.getRecipeBookEmbedBuilder;

public class SelectMenuListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(SelectMenuListener.class);

    private int extractEntityIdFromLabel(String label) {
        int firstHyphenIndex = label.indexOf("-");
        int secondHyphenIndex = label.indexOf("-", firstHyphenIndex + 1);
        String selectedEntityId = label.substring(label.indexOf("-") + 1, secondHyphenIndex);

        return Integer.parseInt(selectedEntityId);
    }

    private int extractEntityIndexFromLabel(String label) {
        int firstHyphenIndex = label.indexOf("-");
        int secondHyphenIndex = label.indexOf("-", firstHyphenIndex + 1);
        String selectedEntityIndex = label.substring(secondHyphenIndex + 1);

        return Integer.parseInt(selectedEntityIndex);
    }

    private int extractPageFromLabel(String label) {
        return Integer.parseInt(label.substring(label.indexOf("-") + 1));
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        String componentId = event.getComponentId();
        logger.info("Selected componentId: {}", componentId);
        Member requester = event.getMember();
        if (requester == null) {
            event.reply("Something went wrong, please try again").setEphemeral(true).queue();
            return;
        }

        String newSelectedValue = event.getValues().get(0);
        EmbedBuilder responseBuilder = null;
        List<ActionRow> actionRows = new ArrayList<>();

        switch (componentId) {
            case "index-select-type" -> {
                logger.info("New Selected Index Type: {}", newSelectedValue);
                String typeName = newSelectedValue.substring(newSelectedValue.indexOf("-") + 1);
                IndexManager.setUserTypeSelection(requester.getId(), typeName);
                IndexManager.setUserPage(requester.getId(), 0);
                IndexManager.setUserSelectedEntityIndex(requester.getId(), 0);
                IndexManager.setUserSelectedEntityId(requester.getId(), -1);
                responseBuilder = getIndexEmbedBuilder(requester);
                actionRows = getActionRowsForResponse(requester);
            }
            case "index-select-entity" -> {
                logger.info("New Selected Index Entity: {}", newSelectedValue);
                IndexManager.setUserSelectedEntityIndex(requester.getId(), extractEntityIndexFromLabel(newSelectedValue));
                IndexManager.setUserSelectedEntityId(requester.getId(), extractEntityIdFromLabel(newSelectedValue));
                responseBuilder = getIndexEmbedBuilder(requester);
                actionRows = getActionRowsForResponse(requester);
            }
            case "index-select-page" -> {
                logger.info("New Selected Index Page: {}", newSelectedValue);
                IndexManager.setUserPage(requester.getId(), extractPageFromLabel(newSelectedValue));
                IndexManager.setUserSelectedEntityIndex(requester.getId(), 0);
                IndexManager.setUserSelectedEntityId(requester.getId(), -1);
                responseBuilder = getIndexEmbedBuilder(requester);
                actionRows = getActionRowsForResponse(requester);
            }
            case "recipe-select-recipe" -> {
                logger.info("New Selected Recipe: {}", newSelectedValue);
                RecipeBookManager.setUserRecipeIndex(requester.getId(), extractEntityIndexFromLabel(newSelectedValue));
                RecipeBookManager.setUserRecipeId(requester.getId(), extractEntityIdFromLabel(newSelectedValue));
                responseBuilder = getRecipeBookEmbedBuilder(requester);
                actionRows = RecipeBookManager.generateActionRows(requester);
            }
            case "recipe-select-page" -> {
                logger.info("New Selected Recipe Page: {}", newSelectedValue);
                RecipeBookManager.setUserPage(requester.getId(), extractPageFromLabel(newSelectedValue));
                RecipeBookManager.setUserRecipeIndex(requester.getId(), 0);
                RecipeBookManager.setUserRecipeId(requester.getId(), -1);
                responseBuilder = getRecipeBookEmbedBuilder(requester);
                actionRows = RecipeBookManager.generateActionRows(requester);
            }
        }

        if (null == responseBuilder) {
            event
                .reply("Something went wrong, please try again")
                .setEphemeral(true)
                .queue();
            return;
        }

        event
            .editMessageEmbeds(responseBuilder.build())
            .setComponents(actionRows)
            .queue();
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        super.onEntitySelectInteraction(event);
    }
}
