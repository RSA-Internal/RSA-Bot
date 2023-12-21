package org.rsa.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.scheduledevent.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.aws.ddb.PutItemResponseWithStatus;
import org.rsa.aws.ddb.dao.ScheduledEventDao;

import java.util.Collections;

public class ScheduledEventListener extends ListenerAdapter {

    @Override
    public void onScheduledEventCreate(@NotNull ScheduledEventCreateEvent event) {
        super.onScheduledEventCreate(event);
        System.out.println("Received event creation for " + event.getResponseNumber());
        Guild guild = event.getGuild();
        ScheduledEvent scheduledEvent = event.getScheduledEvent();
        User creatorAsUser = scheduledEvent.getCreator();

        guild.createRole()
                .setName(scheduledEvent.getId() + "_event_role")
                .setMentionable(false)
                .setPermissions(Collections.emptyList())
                .queue(eventRole -> {
                    PutItemResponseWithStatus response = ScheduledEventDao.write(guild.getId(), scheduledEvent.getId(), eventRole.getId());
                    System.out.println(response.message());
                    if (creatorAsUser != null) {
                        guild.addRoleToMember(creatorAsUser, eventRole).queue();
                    }
                });
    }

    @Override
    public void onScheduledEventDelete(@NotNull ScheduledEventDeleteEvent event) {
        super.onScheduledEventDelete(event);
        Role role = getRoleForEvent(event);
        if (role != null) {
            role.delete().queue();
        }

        ScheduledEventDao.delete(event.getGuild().getId(), event.getScheduledEvent().getId());
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

    private Role getRoleForEvent(@NotNull GenericScheduledEventGatewayEvent event) {
        Guild guild = event.getGuild();
        ScheduledEvent scheduledEvent = event.getScheduledEvent();
        String roleId = ScheduledEventDao.read(guild.getId(), scheduledEvent.getId());
        if (roleId == null) {
            return null;
        }
        return guild.getRoleById(roleId);
    }
}
