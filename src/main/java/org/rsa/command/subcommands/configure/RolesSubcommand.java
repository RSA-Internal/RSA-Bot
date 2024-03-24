package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.constants.GuildConfigurationConstant;
import org.rsa.logic.data.managers.GuildConfigurationManager;

import java.util.Objects;

public class RolesSubcommand extends SubcommandObject {
    public RolesSubcommand()
    {
        super("roles", "Configure server roles.");
        addOptions(
                new OptionData(OptionType.STRING, "role_name", "Specify role to configure", true)
                    .addChoice(GuildConfigurationConstant.MODERATOR_ROLE.getLocalization(), GuildConfigurationConstant.MODERATOR_ROLE.getKey())
                    .addChoice(GuildConfigurationConstant.HELPER_ROLE.getLocalization(), GuildConfigurationConstant.HELPER_ROLE.getKey())
                    .addChoice(GuildConfigurationConstant.RESOLVER_ROLE.getLocalization(), GuildConfigurationConstant.RESOLVER_ROLE.getKey()),
                new OptionData(OptionType.ROLE, "role", "Specify new role", true));
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event)
    {
        Guild guild = event.getGuild();
        if (guild == null) {
            event
                .reply("This command can only be used in a Server.")
                .setEphemeral(true)
                .queue();
            return;
        }
        String option = Objects.requireNonNull(event.getOption("role_name")).getAsString();
        Role newRole = Objects.requireNonNull(event.getOption("role")).getAsRole();
        String value = newRole.getId();
        String response = GuildConfigurationManager.processUpdate(guild, option, value);

        event
            .reply(response)
            .setEphemeral(true)
            .queue();
    }
}

