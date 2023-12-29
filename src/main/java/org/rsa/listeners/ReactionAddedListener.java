package org.rsa.listeners;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.logic.constants.ReputationChanges;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.GuildConfiguration;
import org.rsa.logic.data.models.UserReputation;

public class ReactionAddedListener extends ListenerAdapter {
    private static void giveUpvote(MessageReactionAddEvent event)
    {
        String guildId = event.getGuild().getId();
        UserReputation receiverUserReputation = ReputationManager.fetch(guildId, event.getMessageAuthorId());

        receiverUserReputation.setReceived_post_upvotes(receiverUserReputation.getReceived_post_upvotes() + 1);
        receiverUserReputation.setReputation(receiverUserReputation.getReputation() + ReputationChanges.POST_UPVOTE_RECEIVED);

        ReputationManager.update(receiverUserReputation);
    }

    private static void giveDownvote(MessageReactionAddEvent event)
    {
        String guildId = event.getGuild().getId();
        UserReputation receiverUserReputation = ReputationManager.fetch(guildId, event.getMessageAuthorId());
        UserReputation giverUserReputation = ReputationManager.fetch(guildId, event.getUserId());

        receiverUserReputation.setReceived_post_upvotes(receiverUserReputation.getReceived_post_upvotes() + 1);
        receiverUserReputation.setReputation(receiverUserReputation.getReputation() + ReputationChanges.POST_DOWNVOTE_RECEIVED);

        giverUserReputation.setGiven_post_downvotes(receiverUserReputation.getGiven_post_downvotes() + 1);
        giverUserReputation.setReputation(giverUserReputation.getReputation() + ReputationChanges.POST_DOWNVOTE_GIVEN);

        ReputationManager.update(receiverUserReputation);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event)
    {
        MessageChannelUnion channel = event.getChannel();
        ChannelType channelType = event.getChannelType();
        EmojiUnion reactionEmoji = event.getEmoji();

        if (channelType.equals(ChannelType.GUILD_PUBLIC_THREAD) // channel is a thread
                && channel.asThreadChannel().getParentChannel().getType().equals(ChannelType.FORUM) // channel is a forum post
                && !event.getUserId().equals(event.getMessageAuthorId()) // reactor's id is not equal to the message author's id
                && channel.asThreadChannel().retrieveStartMessage().complete().getId().equals(event.getMessageId())) // is the first message
        { // Is original post in forum channel & reaction was not made by forum author
            GuildConfiguration configuration = GuildConfigurationManager.fetch(event.getGuild().getId());

            if (reactionEmoji.getFormatted().equals(configuration.getUpvote_emoji()))
                giveUpvote(event);
            else if (reactionEmoji.getFormatted().equals(configuration.getDownvote_emoji()))
                giveDownvote(event);
        }
    }
}
