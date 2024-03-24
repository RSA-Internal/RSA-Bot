package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.data.managers.GuildConfigurationManager;

import static org.rsa.logic.constants.GuildConfigurationConstants.*;

public class OptionsSubcommand extends SubcommandObject {

    public OptionsSubcommand() {
        super("options", "Various options for a server.");
        addOptions(
            new OptionData(OptionType.STRING, "option", "Specify the setting to change.", true)
                .addChoice("Required characters for a help thread", REQUIRED_CHARACTERS_KEY)
                .addChoice("Length of help thread title", MESSAGE_CHARACTERS_AS_TITLE_KEY)
                .addChoice("Reputation for Resolve", REPUTATION_FOR_RESOLVE_KEY)
                .addChoice("Reputation for Helping", REPUTATION_FOR_HELPING_KEY)
                .addChoice("Reputation change for receiving an upvote", UPVOTE_RECEIVED_KEY)
                .addChoice("Reputation change for receiving a downvote", DOWNVOTE_RECEIVED_KEY)
                .addChoice("Reputation change for giving a downvote", DOWNVOTE_GIVEN_KEY)
                .addChoice("Reputation change for accepting an answer", ANSWER_ACCEPTED_KEY)
                .addChoice("Reputation change for an answer being accepted", ACCEPTED_ANSWER_KEY)
                .addChoice("Reputation change for a question being moderated", QUESTION_MODERATED_KEY)
                .addChoice("Reputation change for a question being flagged as spam", FLAGGED_SPAM_KEY),
            new OptionData(OptionType.STRING, "value", "The value for the option.", true)
        );
    }

    @Override
    public void handleSubcommand(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event
                .reply("This command can only be used in a Server.")
                .setEphemeral(true)
                .queue();
            return;
        }

        String option = event.getOption("option", OptionMapping::getAsString);
        String value = event.getOption("value", OptionMapping::getAsString);

        if (null == option || null == value) {
            event
                .reply("Something went wrong. Please try again later.")
                .setEphemeral(true)
                .queue();
            return;
        }

        String response = GuildConfigurationManager.processUpdate(guild, option, value);

        event
            .reply(response)
            .setEphemeral(true)
            .queue();
    }
}
