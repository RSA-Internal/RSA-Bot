package org.rsa.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.manager.adventure.IndexManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.rsa.helper.AdventureIndexHelper.getActionRowsForResponse;
import static org.rsa.util.EmbedBuilderUtil.getIndexEmbedBuilder;

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

        switch (componentId) {
            case "index-select-type" -> {
                logger.info("New Selected Type: {}", newSelectedValue);
                String typeName = newSelectedValue.substring(newSelectedValue.indexOf("-") + 1);
                IndexManager.setUserTypeSelection(requester.getId(), typeName);
                IndexManager.setUserPage(requester.getId(), 0);
                IndexManager.setUserSelectedEntityIndex(requester.getId(), 0);
                IndexManager.setUserSelectedEntityId(requester.getId(), -1);
            }
            case "index-select-entity" -> {
                logger.info("New Selected Entity: {}", newSelectedValue);
                IndexManager.setUserSelectedEntityIndex(requester.getId(), extractEntityIndexFromLabel(newSelectedValue));
                IndexManager.setUserSelectedEntityId(requester.getId(), extractEntityIdFromLabel(newSelectedValue));
            }
            case "index-select-page" -> {
                logger.info("New Selected Page: {}", newSelectedValue);
                IndexManager.setUserPage(requester.getId(), extractPageFromLabel(newSelectedValue));
                IndexManager.setUserSelectedEntityIndex(requester.getId(), 0);
                IndexManager.setUserSelectedEntityId(requester.getId(), -1);
            }
        }

        event
            .editMessageEmbeds(getIndexEmbedBuilder(requester).build())
            .setComponents(getActionRowsForResponse(requester))
            .queue();
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        super.onEntitySelectInteraction(event);
    }
}
