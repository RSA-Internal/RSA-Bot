package org.rsa.listeners;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.logic.constants.ReputationChanges;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.UserReputation;

public class ReactionAddedListener extends ListenerAdapter {
    private static final String UPVOTE_EMOJI_CODE = "";
    private static final String UPVOTE_EMOJI_ID = "1188852146272211034";
    private static final String DOWNVOTE_EMOJI_CODE = "U+1F480";
    private static final String DOWNVOTE_EMOJI_ID = "";

    private static boolean isUpvoteReaction(EmojiUnion emoji)
    {
        if (emoji.getType().equals(Emoji.Type.UNICODE))
            return emoji.asUnicode().getAsCodepoints().equals(UPVOTE_EMOJI_CODE);
        else
            return emoji.asCustom().getId().equals(UPVOTE_EMOJI_ID);
    }

    private static boolean isDownvoteReaction(EmojiUnion emoji)
    {
        if (emoji.getType().equals(Emoji.Type.UNICODE))
            return emoji.asUnicode().getAsCodepoints().equals(DOWNVOTE_EMOJI_CODE);
        else
            return emoji.asCustom().getId().equals(DOWNVOTE_EMOJI_ID);
    }

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

        if (channelType.equals(ChannelType.GUILD_PUBLIC_THREAD)
                && channel.asThreadChannel().getParentChannel().getType().equals(ChannelType.FORUM))
        {
            if ( isUpvoteReaction(reactionEmoji) )
                giveUpvote(event);
            else if ( isDownvoteReaction(reactionEmoji) )
                giveDownvote(event);
        }
    }
}
