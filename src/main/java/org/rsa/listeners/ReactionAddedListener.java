package org.rsa.listeners;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
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
        ThreadChannel threadChannel = event.getChannel().asThreadChannel();

        if (threadChannel.retrieveStartMessage().complete().getId().equals(event.getMessageId()))
        { // Is original post
            receiverUserReputation.setReceived_post_upvotes(receiverUserReputation.getReceived_post_upvotes() + 1);
            receiverUserReputation.setReputation(receiverUserReputation.getReputation() + ReputationChanges.POST_UPVOTE_RECEIVED);

            ReputationManager.update(receiverUserReputation);
        }
    }

    private static void giveDownvote(MessageReactionAddEvent event)
    {
        String guildId = event.getGuild().getId();
        UserReputation receiverUserReputation = ReputationManager.fetch(guildId, event.getMessageAuthorId());
        UserReputation giverUserReputation = ReputationManager.fetch(guildId, event.getUserId());
        ThreadChannel threadChannel = event.getChannel().asThreadChannel();

        if (threadChannel.retrieveStartMessage().complete().getId().equals(event.getMessageId()))
        { // Is original post
            receiverUserReputation.setReceived_post_upvotes(receiverUserReputation.getReceived_post_upvotes() + 1);
            receiverUserReputation.setReputation(receiverUserReputation.getReputation() + ReputationChanges.POST_DOWNVOTE_RECEIVED);

            giverUserReputation.setGiven_post_downvotes(receiverUserReputation.getGiven_post_downvotes() + 1);
            giverUserReputation.setReputation(giverUserReputation.getReputation() + ReputationChanges.POST_DOWNVOTE_GIVEN);

            ReputationManager.update(receiverUserReputation);
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event)
    {
        MessageChannelUnion channel = event.getChannel();
        ChannelType channelType = event.getChannelType();
        EmojiUnion reactionEmoji = event.getEmoji();
        GuildConfiguration configuration = GuildConfigurationManager.fetch(event.getGuild().getId());

        if (channelType.equals(ChannelType.GUILD_PUBLIC_THREAD)
                && channel.asThreadChannel().getParentChannel().getType().equals(ChannelType.FORUM)
                && !event.getUserId().equals(event.getMessageAuthorId()))
        {
            if (reactionEmoji.getFormatted().equals(configuration.getUpvote_emoji()))
                giveUpvote(event);
            else if (reactionEmoji.getFormatted().equals(configuration.getDownvote_emoji()))
                giveDownvote(event);
        }
    }
}
