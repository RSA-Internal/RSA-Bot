package org.rsa.listeners;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.logic.constants.ReputationChanges;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.GuildConfiguration;
import org.rsa.logic.data.models.UserReputation;

public class ReactionRemovedListener extends ListenerAdapter {

    private void removeUpvote(MessageReactionRemoveEvent event)
    {
        String guildId = event.getGuild().getId();
        UserReputation receiverUserReputation = ReputationManager.fetch(guildId, event.retrieveMessage().complete().getAuthor().getId());

        receiverUserReputation.setReceived_post_upvotes(receiverUserReputation.getReceived_post_upvotes() - 1);
        receiverUserReputation.setReputation(receiverUserReputation.getReputation() - ReputationChanges.POST_UPVOTE_RECEIVED);

        ReputationManager.update(receiverUserReputation);
    }

    private void removeDownvote(MessageReactionRemoveEvent event)
    {
        String guildId = event.getGuild().getId();
        UserReputation receiverUserReputation = ReputationManager.fetch(guildId, event.retrieveMessage().complete().getAuthor().getId());
        UserReputation giverUserReputation = ReputationManager.fetch(guildId, event.getUserId());

        receiverUserReputation.setReceived_post_downvotes(receiverUserReputation.getReceived_post_downvotes() - 1);
        receiverUserReputation.setReputation(receiverUserReputation.getReputation() - ReputationChanges.POST_DOWNVOTE_RECEIVED);

        giverUserReputation.setGiven_post_downvotes(receiverUserReputation.getGiven_post_downvotes() - 1);
        giverUserReputation.setReputation(giverUserReputation.getReputation() - ReputationChanges.POST_DOWNVOTE_GIVEN);

        ReputationManager.update(receiverUserReputation);
        ReputationManager.update(giverUserReputation);
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event)
    {
        MessageChannelUnion channel = event.getChannel();
        ChannelType channelType = event.getChannelType();
        EmojiUnion reactionEmoji = event.getEmoji();

        if (channelType.equals(ChannelType.GUILD_PUBLIC_THREAD) // channel is a thread
                && channel.asThreadChannel().getParentChannel().getType().equals(ChannelType.FORUM) // channel is a forum post
                && !event.getUserId().equals(event.retrieveMessage().complete().getAuthor().getId()) // reactor's id is not equal to the message author's id
                && channel.asThreadChannel().retrieveStartMessage().complete().getId().equals(event.getMessageId())) // is the first message
        { // Is original post in forum channel & reaction was not made by forum author
            GuildConfiguration configuration = GuildConfigurationManager.fetch(event.getGuild().getId());

            if (reactionEmoji.getFormatted().equals(configuration.getUpvote_emoji()))
                removeUpvote(event);
            else if (reactionEmoji.getFormatted().equals(configuration.getDownvote_emoji()))
                removeDownvote(event);
        }
    }
}
