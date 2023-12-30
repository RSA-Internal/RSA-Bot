package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;

import java.util.Objects;

public class ReactionsSubcommand extends SubcommandObject {
    public ReactionsSubcommand()
    {
        super("reactions", "Change reaction emojis");
        this.addOptions(
                new OptionData(OptionType.STRING, "reaction_type", "Specify the reaction emoji to change.", true)
                    .addChoice("upvote", "upvote")
                    .addChoice("downvote", "downvote"),
                new OptionData(OptionType.STRING, "emoji", "New emoji to assign to reaction emoji", true));
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event) {
        GuildConfiguration config = GuildConfigurationManager.fetch(Objects.requireNonNull(event.getGuild()).getId());
        String reactionType = Objects.requireNonNull(event.getOption("reaction_type")).getAsString();
        String emojiValue = Objects.requireNonNull(event.getOption("emoji")).getAsString();

        if (reactionType.equals("upvote"))
            config.setUpvote_emoji(emojiValue);
        else if (reactionType.equals("downvote"))
            config.setDownvote_emoji(emojiValue);

        try {
            GuildConfigurationManager.update(config);
            event.reply("✅ **`" + reactionType + "`** reaction changed to: " + emojiValue).queue();
        } catch (Exception e)
        {
            event.reply("❎ There was an internal error while saving").queue();
            e.printStackTrace(System.out);
        }
    }
}
