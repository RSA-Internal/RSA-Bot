package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.SubcommandPassthroughObject;
import org.rsa.logic.constants.GuildConfigurationConstant;

import java.util.Objects;

public class RolesSubcommand extends SubcommandPassthroughObject {
    public RolesSubcommand()
    {
        super("roles", "Configure server roles.",
            event -> event.getOption("option", OptionMapping::getAsString),
            event -> Objects.requireNonNull(event.getOption("value", OptionMapping::getAsRole)).getId());
        addOptions(
                new OptionData(OptionType.STRING, "option", "Specify role to configure", true)
                    .addChoice(GuildConfigurationConstant.MODERATOR_ROLE.getLocalization(), GuildConfigurationConstant.MODERATOR_ROLE.getKey())
                    .addChoice(GuildConfigurationConstant.HELPER_ROLE.getLocalization(), GuildConfigurationConstant.HELPER_ROLE.getKey())
                    .addChoice(GuildConfigurationConstant.RESOLVER_ROLE.getLocalization(), GuildConfigurationConstant.RESOLVER_ROLE.getKey()),
                new OptionData(OptionType.ROLE, "value", "Specify new role", true));
    }
}

