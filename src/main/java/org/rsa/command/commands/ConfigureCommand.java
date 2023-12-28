package org.rsa.command.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rsa.command.CommandObject;
import org.rsa.command.SubcommandObject;
import org.rsa.command.subcommands.configure.ReactionsSubcommand;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ConfigureCommand extends CommandObject {
    private static final SubcommandObject[] subcommands = { new ReactionsSubcommand() };

    public ConfigureCommand()
    {
        super("configure", "Configures bot settings for this server.", List.of(), List.of(subcommands));
        setIsGuildOnly();
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event)
    {
        @Nullable String subcommandName = event.getSubcommandName();

        if (subcommandName != null)
        {
            Optional<SubcommandObject> optionalSubcommand = Arrays.stream(subcommands).filter(s -> s.getName().equals(subcommandName)).findFirst();
            optionalSubcommand.ifPresent(subcommandObject -> subcommandObject.handleSubcommand(event));
        }
    }
}
