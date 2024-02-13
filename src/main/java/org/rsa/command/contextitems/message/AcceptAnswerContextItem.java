package org.rsa.command.contextitems.message;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.rsa.command.MessageContextObject;
import org.rsa.logic.constants.ReputationChanges;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.UserReputation;

import java.util.Objects;

public class AcceptAnswerContextItem extends MessageContextObject {
    public AcceptAnswerContextItem()
    {
        super("Accept Answer");
    }

    private void manageReputationChanges(MessageContextInteractionEvent event)
    {
        String guildId = Objects.requireNonNull(event.getGuild()).getId();
        
        UserReputation receiverReputation = ReputationManager.fetch(guildId, event.getTarget().getAuthor().getId());
        UserReputation acceptorReputation = ReputationManager.fetch(guildId, event.getUser().getId());

        receiverReputation.setReceived_accepted_answers(receiverReputation.getReceived_accepted_answers() + 1);
        receiverReputation.setReputation(receiverReputation.getReputation() + ReputationChanges.ANSWER_ACCEPT_RECEIVED);

        acceptorReputation.setGiven_accepted_answers(acceptorReputation.getGiven_accepted_answers() + 1);
        acceptorReputation.setReputation(acceptorReputation.getReputation() + ReputationChanges.ANSWER_ACCEPT_GIVEN);

        ReputationManager.update(receiverReputation);
        ReputationManager.update(acceptorReputation);
    }

    private void acceptAnswer(MessageContextInteractionEvent event)
    {
        Message message = event.getTarget();
        ThreadChannel channel = message.getChannel().asThreadChannel();

        message.addReaction(Emoji.fromUnicode("U+2705")).queue();
        event.reply("Answer accepted").setEphemeral(true).queue();
        message.pin().queue();
        channel.getManager().setLocked(true).setArchived(true).queue();
    }

    @Override
    public void handleInteraction(MessageContextInteractionEvent event)
    {
        ChannelType channelType = event.getChannelType();
        Message message = event.getTarget();
        ThreadChannel channel = message.getChannel().asThreadChannel();
        Message threadStartMessage = channel.retrieveStartMessage().complete();
        User user = event.getUser();

        if (channelType.equals(ChannelType.GUILD_PUBLIC_THREAD) // Is a thread channel
            && !message.equals(threadStartMessage) // Message isn't the original post
            && user.getId().equals(threadStartMessage.getAuthor().getId())) // Acceptor is OP
        {
            if (!message.getAuthor().getId().equals(user.getId())) // If OP didn't accept their own answer
                manageReputationChanges(event);

            acceptAnswer(event);
        }
    }
}
