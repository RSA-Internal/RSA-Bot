package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.SubcommandPassthroughObject;
import org.rsa.logic.constants.GuildConfigurationConstant;

import java.util.Objects;

public class ChannelsSubcommand extends SubcommandPassthroughObject {

    public ChannelsSubcommand() {
        super("channels", "Configure server channels.",
            event -> event.getOption("option", OptionMapping::getAsString),
            event -> Objects.requireNonNull(event.getOption("value", OptionMapping::getAsChannel)).getId());
        addOptions(
            new OptionData(OptionType.STRING, "option", "Specify a channel type to configure.", true)
                .addChoice(GuildConfigurationConstant.HELP_CHANNEL.getLocalization(), GuildConfigurationConstant.HELP_CHANNEL.getKey())
                .addChoice(GuildConfigurationConstant.POLL_CHANNEL.getLocalization(), GuildConfigurationConstant.POLL_CHANNEL.getKey()),
            new OptionData(OptionType.CHANNEL, "value", "Specify the channel", true)
        );
    }
}
