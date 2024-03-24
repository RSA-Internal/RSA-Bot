package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.SubcommandPassthroughObject;
import org.rsa.logic.constants.GuildConfigurationConstant;

public class OptionsSubcommand extends SubcommandPassthroughObject {

    public OptionsSubcommand() {
        super("options", "Various options for a server.",
            event -> event.getOption("option", OptionMapping::getAsString),
            event -> event.getOption("value", OptionMapping::getAsString));
        addOptions(
            new OptionData(OptionType.STRING, "option", "Specify the setting to change.", true)
                .addChoice(GuildConfigurationConstant.REQUIRED_CHARACTERS.getLocalization(), GuildConfigurationConstant.REQUIRED_CHARACTERS.getKey())
                .addChoice(GuildConfigurationConstant.HELP_TITLE_LENGTH.getLocalization(), GuildConfigurationConstant.HELP_TITLE_LENGTH.getKey())
                .addChoice(GuildConfigurationConstant.RESOLVER_REPUTATION.getLocalization(), GuildConfigurationConstant.RESOLVER_REPUTATION.getKey())
                .addChoice(GuildConfigurationConstant.HELPER_REPUTATION.getLocalization(), GuildConfigurationConstant.HELPER_REPUTATION.getKey())
                .addChoice(GuildConfigurationConstant.UPVOTE_RECEIVED.getLocalization(), GuildConfigurationConstant.UPVOTE_RECEIVED.getKey())
                .addChoice(GuildConfigurationConstant.DOWNVOTE_RECEIVED.getLocalization(), GuildConfigurationConstant.DOWNVOTE_RECEIVED.getKey())
                .addChoice(GuildConfigurationConstant.DOWNVOTE_GIVEN.getLocalization(), GuildConfigurationConstant.DOWNVOTE_GIVEN.getKey())
                .addChoice(GuildConfigurationConstant.ANSWER_ACCEPTED.getLocalization(), GuildConfigurationConstant.ANSWER_ACCEPTED.getKey())
                .addChoice(GuildConfigurationConstant.ACCEPTED_ANSWER.getLocalization(), GuildConfigurationConstant.ACCEPTED_ANSWER.getKey())
                .addChoice(GuildConfigurationConstant.QUESTION_MODERATED.getLocalization(), GuildConfigurationConstant.QUESTION_MODERATED.getKey())
                .addChoice(GuildConfigurationConstant.FLAGGED_SPAM.getLocalization(), GuildConfigurationConstant.FLAGGED_SPAM.getKey()),
            new OptionData(OptionType.STRING, "value", "The value for the option.", true)
        );
    }
}
