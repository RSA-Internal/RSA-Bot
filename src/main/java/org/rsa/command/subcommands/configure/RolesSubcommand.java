package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;

import java.util.Objects;

public class RolesSubcommand extends SubcommandObject {
    public RolesSubcommand()
    {
        super("roles", "Configure server roles.");
        addOptions(
                new OptionData(OptionType.STRING, "role_name", "Specify role to configure", true)
                    .addChoice("moderator", "moderator"),
                new OptionData(OptionType.ROLE, "role", "Specify new role", true));
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event)
    {
        String roleName = Objects.requireNonNull(event.getOption("role_name")).getAsString();
        Role newRole = Objects.requireNonNull(event.getOption("role")).getAsRole();
        GuildConfiguration guildConfig = GuildConfigurationManager.fetch(Objects.requireNonNull(event.getGuild()).getId());

        if (roleName.equals("moderator")) {
            guildConfig.setModerator_role_id(newRole.getId());
        }

        GuildConfigurationManager.update(guildConfig);
        event.reply("✅ **" + roleName + "** role changed to " + newRole.getAsMention()).queue();
    }
}
