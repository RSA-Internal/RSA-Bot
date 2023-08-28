package org.rsa.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.Commands;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (event.getGuild() == null) {
            return;
        }

        Commands.getCommand(event.getName()).onSlashCommandInteraction(event);
    }
}