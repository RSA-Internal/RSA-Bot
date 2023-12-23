package org.rsa.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.scheduledevent.*;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.aws.ddb.dao.ScheduledEventDao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduledEventListener extends ListenerAdapter {

    private static final Map<ScheduledEvent.Status, String> STATUS_MESSAGE_MAP = new HashMap<>() {{
        put(ScheduledEvent.Status.SCHEDULED, "%s, Event `%s` has been scheduled.");
        put(ScheduledEvent.Status.CANCELED, "%s, Event `%s` has been cancelled.");
        put(ScheduledEvent.Status.ACTIVE, "%s, Event `%s` is starting.");
        put(ScheduledEvent.Status.COMPLETED, "%s, Event `%s` is now complete.");
    }};

    @Override
    public void onScheduledEventCreate(@NotNull ScheduledEventCreateEvent event) {
        super.onScheduledEventCreate(event);
        setupEvent(event.getGuild(), event.getScheduledEvent());
    }

    @Override
    public void onScheduledEventDelete(@NotNull ScheduledEventDeleteEvent event) {
        super.onScheduledEventDelete(event);
        sendMessageForEvent(event.getScheduledEvent(), ScheduledEvent.Status.CANCELED);
        cleanupEvent(event.getScheduledEvent());
    }

    @Override
    public void onScheduledEventUserAdd(@NotNull ScheduledEventUserAddEvent event) {
        super.onScheduledEventUserAdd(event);
        Role role = getRoleForEvent(event.getScheduledEvent());
        Guild guild = event.getGuild();
        Member member = event.getMember();
        if (role == null || member == null) {
            return;
        }
        guild.addRoleToMember(member, role).queue();
    }

    @Override
    public void onScheduledEventUserRemove(@NotNull ScheduledEventUserRemoveEvent event) {
        super.onScheduledEventUserRemove(event);
        Role role = getRoleForEvent(event.getScheduledEvent());
        Guild guild = event.getGuild();
        Member member = event.getMember();
        if (role == null || member == null) {
            return;
        }
        guild.removeRoleFromMember(member, role).queue();
    }

    @Override
    public void onScheduledEventUpdateStatus(@NotNull ScheduledEventUpdateStatusEvent event) {
        super.onScheduledEventUpdateStatus(event);
        sendMessageForEvent(event.getScheduledEvent(), event.getNewStatus());

        if (event.getNewStatus() == ScheduledEvent.Status.COMPLETED) {
            cleanupEvent(event.getScheduledEvent());
        }
    }

    private static Role getRoleForEvent(ScheduledEvent event) {
        Guild guild = event.getGuild();
        String roleId = ScheduledEventDao.read(guild.getId(), event.getId(), "roleid");
        if (roleId == null) {
            return null;
        }
        return guild.getRoleById(roleId);
    }

    private void cleanupEvent(ScheduledEvent event) {
        Role role = getRoleForEvent(event);
        if (role != null) {
            role.delete().queue();
        }

        String guildId = event.getGuild().getId();
        String eventId = event.getId();
        List<String> messageIdL = ScheduledEventDao.getMessageListForEvent(guildId, eventId);
        TextChannel channel = getTextChannel(event.getGuild());
        if (channel == null) {
            return;
        }
        if (messageIdL.isEmpty()) {
            return;
        }

        if (messageIdL.size() < 2) {
            channel.deleteMessageById(messageIdL.get(0)).queue();
        } else {
            channel.deleteMessagesByIds(messageIdL).queue();
        }

        ScheduledEventDao.delete(guildId, eventId);
    }

    public static boolean setupEvent(Guild guild, ScheduledEvent event) {
        Role role = getRoleForEvent(event);
        if (role != null) {
            return false;
        }

        Role eventRole = guild.createRole()
                .setName(event.getId() + "_event_role")
                .setMentionable(false)
                .setPermissions(Collections.emptyList())
                .complete();
        ScheduledEventDao.writeEventRole(guild.getId(), event.getId(), eventRole.getId());
        event.retrieveInterestedMembers().queue(members -> members.forEach(member -> guild.addRoleToMember(member, eventRole).queue()));

        sendMessageForEvent(event, ScheduledEvent.Status.SCHEDULED);
        return true;
    }

    private static TextChannel getTextChannel(Guild guild) {
        String channelId = ScheduledEventDao.read(guild.getId(), "null", "channelid");
        if (channelId == null) {
            return null;
        }
        return guild.getTextChannelById(channelId);
    }

    private static void sendMessageForEvent(ScheduledEvent event, ScheduledEvent.Status status) {
        String eventMessage = STATUS_MESSAGE_MAP.get(status);
        TextChannel channel = getTextChannel(event.getGuild());
        Role role = getRoleForEvent(event);
        if (channel == null || role == null) {
            return;
        }

        Message message = channel
                .sendMessage(String.format(eventMessage, role.getAsMention(), event.getName()))
                .complete();
        ScheduledEventDao.updateMessageListForEvent(
                event.getGuild().getId(),
                event.getId(),
                message.getId());
    }
}
