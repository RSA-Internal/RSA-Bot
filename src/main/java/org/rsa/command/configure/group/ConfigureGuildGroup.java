package org.rsa.command.configure.group;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.rsa.command.configure.subcommand.*;

public class ConfigureGuildGroup extends SubcommandGroupData {

    public ConfigureGuildGroup() {
        super("guild", "Configure Guild settings");
        addSubcommands(
            new ConfigureChannels(),
            new ConfigureOptions(),
            new ConfigureReactions(),
            new ConfigureRoles(),
            new ConfigureView()
        );
    }
}
