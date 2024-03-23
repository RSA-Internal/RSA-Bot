package org.rsa.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.CommandObject;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;

import static org.rsa.util.CheckInToggleUtil.toggleUserCheckInState;

public class CheckOutCommand extends CommandObject {

    public CheckOutCommand() {
        super("check-out", "Toggle check-out status for the Helper role for this server.");
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event
                .reply("This command can only be used in a Server.")
                .setEphemeral(true)
                .queue();
            return;
        }
        GuildConfiguration guildConfiguration = GuildConfigurationManager.fetch(guild.getId());
        String helperRoleId = guildConfiguration.getHelper_role_id();

        if (helperRoleId.isEmpty() || helperRoleId.isBlank()) {
            event
                .reply(guild.getName() + " does not have an active helper role. You were not checked-out.")
                .setEphemeral(true)
                .queue();
            return;
        }

        Role role = guild.getRoleById(helperRoleId);

        if (null == role) {
            event
                .reply(guild.getName() + "'s helper role is misconfigured. You were not checked-out.")
                .setEphemeral(true)
                .queue();
            return;
        }

        Member member = event.getMember();
        if (null == member) {
            event
                .reply("An error occurred and you were not checked-out. Please try again later.")
                .setEphemeral(true)
                .queue();
            return;
        }

        toggleUserCheckInState(event, guild, member, role);
    }
}
