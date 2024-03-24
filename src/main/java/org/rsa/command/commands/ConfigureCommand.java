package org.rsa.command.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.CommandObject;
import org.rsa.command.SubcommandPassthroughObject;
import org.rsa.command.subcommands.configure.*;

import java.util.List;

public class ConfigureCommand extends CommandObject {
    private static final SubcommandPassthroughObject[] subcommands = {
        new ReactionsSubcommand(),
        new RolesSubcommand(),
        new ChannelsSubcommand(),
        new OptionsSubcommand(),
        new ViewConfigSubcommand()
    };

    public ConfigureCommand()
    {
        super("configure", "Configures bot settings for this server.", List.of(), List.of(subcommands));
        setIsGuildOnly();
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        processSubcommand(event, subcommands, event.getSubcommandName());
    }
}
