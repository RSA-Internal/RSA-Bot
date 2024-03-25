package org.rsa.command.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.CommandObject;
import org.rsa.command.SubcommandObject;
import org.rsa.command.subcommands.adventure.AdventureInventorySubcommand;
import org.rsa.exception.ValidationException;

import java.util.Collections;
import java.util.List;

public class AdventureCommand extends CommandObject {

    private static final SubcommandObject[] subcommands = {
        new AdventureInventorySubcommand(),
    };

    public AdventureCommand() {
        super("adventure", "Various commands for the adventure side of PandaBot", Collections.emptyList(), List.of(subcommands));
        setIsGuildOnly();
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) throws ValidationException {
        processSubcommand(event, subcommands, event.getSubcommandName());
    }
}
