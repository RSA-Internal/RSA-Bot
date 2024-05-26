package org.rsa.command.configure.subcommand.devforum;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.panda.jda.command.SubcommandObjectV2;

public class ConfigureDevforumEnabled extends SubcommandObjectV2 {

    public ConfigureDevforumEnabled() {
        super("enabled", "Enable/disable devforum updates");
        addOptions(new OptionData(OptionType.BOOLEAN, "enabled", "Enable or disable devforum updates", true));
    }
}
