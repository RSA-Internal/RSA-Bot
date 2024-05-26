package org.rsa.command.configure;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.configure.group.ConfigureDevforumUpdates;
import org.rsa.command.configure.group.ConfigureGuildGroup;

public class ConfigureCommand extends CommandObjectV2 {

    public ConfigureCommand() {
        super("configure", "Configure command");
        addSubcommandGroup(new ConfigureGuildGroup());
        addSubcommandGroup(new ConfigureDevforumUpdates());
    }
}
