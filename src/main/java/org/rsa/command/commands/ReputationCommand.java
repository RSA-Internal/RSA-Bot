package org.rsa.command.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.CommandObject;
import org.rsa.command.SubcommandObject;
import org.rsa.command.subcommands.reputation.ViewSubcommand;

import java.util.List;

public class ReputationCommand extends CommandObject {

    private static final SubcommandObject[] subcommands = {
        new ViewSubcommand()
    };

    public ReputationCommand() {
        super("reputation", "Various reputation commands for this server.", List.of(), List.of(subcommands));
        setIsGuildOnly();
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        processSubcommand(event, subcommands, event.getSubcommandName());
    }
}
