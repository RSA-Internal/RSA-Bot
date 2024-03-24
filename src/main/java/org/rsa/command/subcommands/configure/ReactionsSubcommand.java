package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.data.managers.GuildConfigurationManager;

import java.util.Objects;

import static org.rsa.logic.constants.GuildConfigurationConstants.*;

public class ReactionsSubcommand extends SubcommandObject {
    public ReactionsSubcommand()
    {
        super("reactions", "Change reaction emojis");
        this.addOptions(
                new OptionData(OptionType.STRING, "reaction_type", "Specify the reaction emoji to change.", true)
                    .addChoice("upvote", UPVOTE_EMOJI_KEY)
                    .addChoice("downvote", DOWNVOTE_EMOJI_KEY)
                    .addChoice("moderate", MODERATE_EMOJI_KEY),
                new OptionData(OptionType.STRING, "emoji", "New emoji to assign to reaction emoji", true));
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event
                .reply("This command can only be used in a Server.")
                .setEphemeral(true)
                .queue();
            return;
        }

        String option = Objects.requireNonNull(event.getOption("reaction_type")).getAsString();
        String value = Objects.requireNonNull(event.getOption("emoji")).getAsString();
        String response = GuildConfigurationManager.processUpdate(guild, option, value);

        event
            .reply(response)
            .setEphemeral(true)
            .queue();
    }
}
