package org.rsa.translator;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.rsa.beans.UserReputation;
import org.rsa.util.HelperUtil;

public class ReputationTranslator {

    public static MessageEmbed getReputationAsEmbed(Guild guild, UserReputation reputation, Member requester, Member reputationHolder) {
        System.out.println("User avatar url: " + reputationHolder.getEffectiveAvatarUrl());

        return new EmbedBuilder()
            .setAuthor(requester.getEffectiveName())
            .setTitle("Reputation for " + reputationHolder.getEffectiveName() + ": " + reputation.getReputation())
            .addField("Given Upvotes", String.valueOf(reputation.getGiven_post_upvotes()), true)
            .addField("Received Upvotes", String.valueOf(reputation.getReceived_post_upvotes()), true)
            .addBlankField(true)
            .addField("Given Downvotes", String.valueOf(reputation.getGiven_post_downvotes()), true)
            .addField("Received Downvotes", String.valueOf(reputation.getReceived_post_downvotes()), true)
            .addBlankField(true)
            .addField("Given Moderations", String.valueOf(reputation.getGiven_moderations()), true)
            .addField("Received Moderations", String.valueOf(reputation.getReceived_moderations()), true)
            .addBlankField(true)
            .addField("Given Spam Flags", String.valueOf(reputation.getGiven_spam_flags()), true)
            .addField("Received Spam Flags", String.valueOf(reputation.getReceived_spam_flags()), true)
            .addBlankField(true)
            .addField("Accepted Answers", String.valueOf(reputation.getAccepted_answers()), true)
            .addField("Answers Accepted", String.valueOf(reputation.getOther_answers_accepted()), true)
            .addBlankField(true)
            .addField("Times Helped", String.valueOf(reputation.getTimes_helped()), true)
            .setColor(HelperUtil.getRandomColor())
            .setFooter("Position on Leaderboard in " + guild.getName() + ": unranked")
            .setThumbnail(reputationHolder.getEffectiveAvatarUrl())
            .build();
    }
}
