package org.rsa.command.configure.subcommand.devforum;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.panda.jda.command.SubcommandObjectV2;

public class ConfigureDevforumChannel extends SubcommandObjectV2 {

    public ConfigureDevforumChannel() {
        super("channel", "Configure update channel.");

        addOptions(
            new OptionData(OptionType.CHANNEL, "channel", "Specify the update channel", true));
    }
}
