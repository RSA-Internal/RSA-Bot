package org.rsa.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CheckInToggleUtil {
    public static void toggleUserCheckInState(SlashCommandInteractionEvent event, Guild guild, Member member, Role role) {
        if (member.getRoles().contains(role)) {
            guild.removeRoleFromMember(member, role).queue(
                s -> event
                    .reply("You were successfully checked-out.")
                    .setEphemeral(true)
                    .queue(),
                e -> event
                    .reply("An error occurred and you were not checked-out. Please try again later.")
                    .setEphemeral(true)
                    .queue()
            );
        } else {
            guild.addRoleToMember(member, role).queue(
                s -> event
                    .reply("You were successfully checked-in.")
                    .setEphemeral(true)
                    .queue(),
                e -> event
                    .reply("An error occurred and you were not checked-in. Please try again later.")
                    .setEphemeral(true)
                    .queue()
            );
        }
    }
}
