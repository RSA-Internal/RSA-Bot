package org.rsa.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.Commands;
import org.rsa.command.v2.CommandObjectV2;

import java.util.Objects;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (event.getGuild() == null || event.isAcknowledged()) {
            return;
        }

        CommandObjectV2 commandObjectV2 = Commands.getCommandV2(event.getName());
        if (Objects.nonNull(commandObjectV2)) {
            commandObjectV2.onSlashCommandInteraction(event);
        }

        if (!event.isAcknowledged()) {
            event.reply("Failed to acknowledge event, please let the bot author know.").setEphemeral(true).queue();
        }
    }
}