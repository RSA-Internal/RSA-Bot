package org.rsa.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.Commands;
import org.rsa.exception.ValidationException;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (event.getGuild() == null) {
            return;
        }

        try {
            Commands.getCommand(event.getName()).handleSlashCommand(event);
        } catch (ValidationException e) {
            event.reply(e.toEventResponse()).setEphemeral(true).queue();
        }
    }
}