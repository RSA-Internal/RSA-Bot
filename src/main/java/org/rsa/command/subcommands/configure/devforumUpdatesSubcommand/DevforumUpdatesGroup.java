package org.rsa.command.subcommands.configure.devforumUpdatesSubcommand;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class DevforumUpdatesGroup extends SubcommandGroupData {
    public DevforumUpdatesGroup() {
        super("devforum", "Configure Devforum Updates");
        addSubcommands(new SubscribeSubcommand());
    }
}
