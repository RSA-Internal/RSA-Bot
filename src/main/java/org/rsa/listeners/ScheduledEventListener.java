package org.rsa.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.scheduledevent.*;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.aws.ddb.PutItemResponseWithStatus;
import org.rsa.aws.ddb.dao.ScheduledEventDao;

import java.util.Collections;
import java.util.HashMap;
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
        Guild guild = event.getGuild();
        ScheduledEvent scheduledEvent = event.getScheduledEvent();
        User creatorAsUser = scheduledEvent.getCreator();

        guild.createRole()
                .setName(scheduledEvent.getId() + "_event_role")
                .setMentionable(false)
                .setPermissions(Collections.emptyList())
                .queue(eventRole -> {
                    PutItemResponseWithStatus response = ScheduledEventDao.writeEventRole(guild.getId(), scheduledEvent.getId(), eventRole.getId());
                    if (creatorAsUser != null) {
                        guild.addRoleToMember(creatorAsUser, eventRole).queue();
                    }
                    sendMessageForEvent(event, ScheduledEvent.Status.SCHEDULED);
                });
    }

    @Override
    public void onScheduledEventDelete(@NotNull ScheduledEventDeleteEvent event) {
        super.onScheduledEventDelete(event);
        cleanupEvent(event);
        sendMessageForEvent(event, ScheduledEvent.Status.CANCELED);
    }

    @Override
    public void onScheduledEventUserAdd(@NotNull ScheduledEventUserAddEvent event) {
        super.onScheduledEventUserAdd(event);
        Role role = getRoleForEvent(event);
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
        Role role = getRoleForEvent(event);
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
        sendMessageForEvent(event, event.getNewStatus());

        if (event.getNewStatus() == ScheduledEvent.Status.COMPLETED) {
            cleanupEvent(event);
        }
    }

    private Role getRoleForEvent(@NotNull GenericScheduledEventGatewayEvent event) {
        Guild guild = event.getGuild();
        ScheduledEvent scheduledEvent = event.getScheduledEvent();
        String roleId = ScheduledEventDao.read(guild.getId(), scheduledEvent.getId());
        if (roleId == null) {
            return null;
        }
        return guild.getRoleById(roleId);
    }

    private void cleanupEvent(@NotNull GenericScheduledEventGatewayEvent event) {
        Role role = getRoleForEvent(event);
        if (role != null) {
            role.delete().queue();
        }

        ScheduledEventDao.delete(event.getGuild().getId(), event.getScheduledEvent().getId());
    }

    private TextChannel getTextChannel(Guild guild) {
        String channelId = ScheduledEventDao.read(guild.getId(), "null");
        if (channelId == null) {
            return null;
        }
        return guild.getTextChannelById(channelId);
    }

    private void sendMessageForEvent(@NotNull GenericScheduledEventGatewayEvent event, ScheduledEvent.Status status) {
        String eventMessage = STATUS_MESSAGE_MAP.get(status);
        TextChannel channel = getTextChannel(event.getGuild());
        Role role = getRoleForEvent(event);
        if (channel == null || role == null) {
            return;
        }
        channel.sendMessage(String.format(eventMessage, role.getAsMention(), event.getScheduledEvent().getName())).queue();
    }
}
