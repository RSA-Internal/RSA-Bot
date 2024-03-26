package org.rsa.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;
import org.rsa.adventure.IndexManager;

import static org.rsa.adventure.IndexManager.*;

public class SelectMenuListener extends ListenerAdapter {

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
            String typeName = newSelectedType.substring(newSelectedType.indexOf("-") + 1);
            IndexManager.setUserTypeSelection(requester.getId(), typeName);

            // TODO: if there are more than 25 entities for a type, display a paging option.

            event
                .editMessageEmbeds(getIndexEmbed(requester, typeName, typeName + "-1"))
                .setComponents(
                    ActionRow.of(getIndexSelectType(requester)),
                    ActionRow.of(getIndexSelectEntity(requester))
                )
                .queue();
        } else if(componentId.equals("index-select-entity")) {
            String currentSelectedType = IndexManager.getUserTypeSelection(requester.getId());
            String newSelectedEntity = event.getValues().get(0);

            event
                .editMessageEmbeds(getIndexEmbed(requester, currentSelectedType, newSelectedEntity))
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
