package org.rsa.command.v2.configure;

import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.v2.configure.group.ConfigureGuildGroup;

public class ConfigureCommandV2 extends CommandObjectV2 {

    public ConfigureCommandV2() {
        super("configure", "Configure command");
        addSubcommandGroup(new ConfigureGuildGroup());
    }
}
