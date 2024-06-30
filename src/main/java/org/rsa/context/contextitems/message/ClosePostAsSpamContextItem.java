package org.rsa.context.contextitems.message;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.rsa.beans.GuildConfiguration;
import org.rsa.beans.UserReputation;
import org.rsa.context.MessageContextObject;
import org.rsa.managers.GuildConfigurationManager;
import org.rsa.managers.ReputationManager;
import org.rsa.util.GuildConfigurationConstant;

import java.util.List;
import java.util.Objects;

import static org.rsa.util.ConversionUtil.parseIntFromString;

public class ClosePostAsSpamContextItem extends MessageContextObject {

    public ClosePostAsSpamContextItem()
    {
        super("Close As Spam");
    }

    private boolean findRole(Member member, String id) {
        List<Role> roles = member.getRoles();
        return roles.stream()
                .anyMatch(role -> role.getId().equals(id));
    }

    private boolean userHasPermission(String guildId, Member member)
    {
        GuildConfiguration guildConfig = GuildConfigurationManager.fetch(guildId);
        return findRole(member, guildConfig.getModerator_role_id());
    }

    private void updateReputations(MessageContextInteractionEvent event)
    {
        Message message = event.getTarget();
        User poster = message.getAuthor();
        String guildId = Objects.requireNonNull(event.getGuild()).getId();
        GuildConfiguration guildConfiguration = GuildConfigurationManager.fetch(guildId);

        UserReputation posterReputation = ReputationManager.fetch(guildId, poster.getId());
        posterReputation.setReceived_spam_flags(posterReputation.getReceived_spam_flags() + 1);
        posterReputation.setReputation(posterReputation.getReputation() + parseIntFromString(guildConfiguration.getValue(GuildConfigurationConstant.FLAGGED_SPAM.getKey())));

        ReputationManager.update(posterReputation);
    }

    @Override
    public void handleInteraction(MessageContextInteractionEvent event)
    {
        MessageChannelUnion channel = event.getChannel();
        ChannelType channelType = event.getChannelType();
        String guildId = Objects.requireNonNull(event.getGuild()).getId();
        Member member = event.getMember();

        if (member != null && channel != null
                && channelType.isThread()
                && channel.asThreadChannel().getParentChannel().getType().equals(ChannelType.FORUM) // is a forum thread
                && event.getTarget().equals(channel.asThreadChannel().retrieveStartMessage()) // is first message
                && userHasPermission(guildId, member))
        {
            updateReputations(event);
            channel.asThreadChannel().delete().queue();
        }
    }
}
