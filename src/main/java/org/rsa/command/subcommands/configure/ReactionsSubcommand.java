package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.SubcommandPassthroughObject;
import org.rsa.logic.constants.GuildConfigurationConstant;

public class ReactionsSubcommand extends SubcommandPassthroughObject {
    public ReactionsSubcommand()
    {
        super("reactions", "Change reaction emojis",
            event -> event.getOption("option", OptionMapping::getAsString),
            event -> event.getOption("value", OptionMapping::getAsString));
        this.addOptions(
                new OptionData(OptionType.STRING, "option", "Specify the reaction emoji to change.", true)
                    .addChoice(GuildConfigurationConstant.UPVOTE_EMOJI.getLocalization(), GuildConfigurationConstant.UPVOTE_EMOJI.getKey())
                    .addChoice(GuildConfigurationConstant.DOWNVOTE_EMOJI.getLocalization(), GuildConfigurationConstant.DOWNVOTE_EMOJI.getKey())
                    .addChoice(GuildConfigurationConstant.MODERATE_EMOJI.getLocalization(), GuildConfigurationConstant.MODERATE_EMOJI.getKey())
                    .addChoice(GuildConfigurationConstant.ACCEPT_EMOJI.getLocalization(), GuildConfigurationConstant.ACCEPT_EMOJI.getKey()),
                new OptionData(OptionType.STRING, "value", "New emoji to assign to reaction emoji", true));
    }
}
