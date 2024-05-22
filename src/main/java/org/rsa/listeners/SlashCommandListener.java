package org.rsa.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.Commands;

import java.util.Objects;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (event.getGuild() == null || event.isAcknowledged()) {
            return;
        }

        CommandObjectV2 commandObjectV2 = Commands.getCommand(event.getName());
        if (Objects.nonNull(commandObjectV2)) {
            commandObjectV2.onSlashCommandInteraction(event);
        }

        if (!event.isAcknowledged()) {
            event.reply("Failed to acknowledge event, please let the bot author know.").setEphemeral(true).queue();
        }
    }
}