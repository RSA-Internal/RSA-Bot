package org.rsa.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;
import org.rsa.adventure.IndexManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.rsa.adventure.IndexManager.*;
import static org.rsa.util.EmbedBuilderUtil.getIndexEmbedBuilder;

public class SelectMenuListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(SelectMenuListener.class);

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        String componentId = event.getComponentId();
        Member requester = event.getMember();
        if (requester == null) {
            event.reply("Something went wrong, please try again").setEphemeral(true).queue();
            return;
        }

        if (componentId.equals("index-select-type")) {
            String newSelectedType = event.getValues().get(0);
            logger.info("New Type: " + newSelectedType);
            String typeName = newSelectedType.substring(newSelectedType.indexOf("-") + 1);
            IndexManager.setUserTypeSelection(requester.getId(), typeName);

            // TODO: if there are more than 25 entities for a type, display a paging option.

            event
                .editMessageEmbeds(getIndexEmbedBuilder(requester, typeName, typeName + "-1").build())
                .setComponents(
                    ActionRow.of(getIndexSelectType(requester)),
                    ActionRow.of(getIndexSelectEntity(requester))
                )
                .queue();
        } else if(componentId.equals("index-select-entity")) {
            String currentSelectedType = IndexManager.getUserTypeSelection(requester.getId());
            String newSelectedEntity = event.getValues().get(0);
            logger.info("New Entity: " + newSelectedEntity);

            event
                .editMessageEmbeds(getIndexEmbedBuilder(requester, currentSelectedType, newSelectedEntity).build())
                .setComponents(
                    ActionRow.of(getIndexSelectType(requester)),
                    ActionRow.of(getIndexSelectEntity(requester, newSelectedEntity))
                )
                .queue();
        }
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        super.onEntitySelectInteraction(event);
    }
}
