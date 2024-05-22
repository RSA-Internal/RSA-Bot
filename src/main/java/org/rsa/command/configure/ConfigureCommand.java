package org.rsa.command.configure;

import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.configure.group.ConfigureGuildGroup;

public class ConfigureCommand extends CommandObjectV2 {

    public ConfigureCommand() {
        super("configure", "Configure command");
        addSubcommandGroup(new ConfigureGuildGroup());
    }
}
