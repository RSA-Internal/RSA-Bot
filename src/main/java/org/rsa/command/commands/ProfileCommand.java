package org.rsa.command.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rsa.command.CommandObject;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.UserReputation;

import java.awt.*;
import java.util.Objects;

public class ProfileCommand extends CommandObject {
    public ProfileCommand()
    {
        super("profile", "View reputation profile");
        addOptionData(new OptionData(OptionType.USER, "user", "Specify user for which data is to be fetched.", false));
        setIsGuildOnly();
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        @Nullable OptionMapping optionSpecifiedUser = event.getOption("user");
        User targetUser = optionSpecifiedUser != null ? optionSpecifiedUser.getAsUser() : event.getUser();
        UserReputation reputation = ReputationManager.fetch(Objects.requireNonNull(event.getGuild()).getId(), targetUser.getId());

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Reputation")
                .setAuthor(targetUser.getGlobalName(), targetUser.getAvatarUrl(), targetUser.getAvatarUrl())
                .addField("Given Upvotes", reputation.getGiven_post_upvotes().toString(), true)
                .addField("Given Moderations", reputation.getGiven_moderations().toString(), true)
                .addField("Given Downvotes", reputation.getGiven_post_downvotes().toString(), true)
                .addField("Given Spam Flags", reputation.getGiven_spam_flags().toString(), true)
                .addField("Given Accepted Answers", reputation.getGiven_accepted_answers().toString(), true)
                .addField("Received Upvotes", reputation.getReceived_post_upvotes().toString(), true)
                .addField("Received Moderations", reputation.getReceived_moderations().toString(), true)
                .addField("Received Downvotes", reputation.getReceived_post_downvotes().toString(), true)
                .addField("Received Spam Flags", reputation.getReceived_spam_flags().toString(), true)
                .addField("Received Accepted Answers", reputation.getReceived_accepted_answers().toString(), true)
                .setColor(new Color(252, 183, 78))
                .setFooter("Id: " + targetUser.getId())
                .build();

        event.replyEmbeds(embed).queue();
    }
}
