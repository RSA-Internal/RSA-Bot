package org.rsa.command.configure.group;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.rsa.command.configure.subcommand.devforum.ConfigureDevforumChannel;
import org.rsa.command.configure.subcommand.devforum.ConfigureDevforumEnabled;
import org.rsa.command.configure.subcommand.devforum.ConfigureDevforumSubscribed;

public class ConfigureDevforumUpdates extends SubcommandGroupData {

    public ConfigureDevforumUpdates() {
        super("devforumupdates", "Configure devforum updates");
        addSubcommands(
                new ConfigureDevforumSubscribed(),
                new ConfigureDevforumEnabled(),
                new ConfigureDevforumChannel()
        );
    }
}
