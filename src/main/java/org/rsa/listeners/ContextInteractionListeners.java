package org.rsa.listeners;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.ContextItems;
import org.rsa.exception.ValidationException;

public class ContextInteractionListeners extends ListenerAdapter {
    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event)
    {
        if (event.getGuild() == null || event.isAcknowledged()) {
            return;
        }

        try {
            ContextItems.getLoadedUserItems().get(event.getName()).handleInteraction(event);
        } catch (ValidationException e) {
            event.reply(e.toEventResponse()).setEphemeral(true).queue();
        }
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event)
    {
        if (event.getGuild() == null || event.isAcknowledged()) {
            return;
        }

        try {
            ContextItems.getLoadedMessageItems().get(event.getName()).handleInteraction(event);
        } catch (ValidationException e) {
            event.reply(e.toEventResponse()).setEphemeral(true).queue();
        }
    }
}
