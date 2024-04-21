package org.rsa.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.CommandObject;
import org.rsa.command.Commands;
import org.rsa.command.v2.CommandObjectV2;
import org.rsa.exception.ValidationException;

import java.util.Objects;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        if (event.getGuild() == null || event.isAcknowledged()) {
            return;
        }

        CommandObject command = Commands.getCommand(event.getName());
        if (Objects.nonNull(command)) {
            try {
                command.handleSlashCommand(event);
            } catch (ValidationException e) {
                event.reply("Failed to process interaction.").setEphemeral(true).queue();
            }
            return;
        }

        CommandObjectV2 commandObjectV2 = Commands.getCommandV2(event.getName());
        if (Objects.nonNull(commandObjectV2)) {
            commandObjectV2.onSlashCommandInteraction(event);
        }
    }
}