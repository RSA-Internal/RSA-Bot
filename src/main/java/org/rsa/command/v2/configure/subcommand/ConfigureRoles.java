package org.rsa.command.v2.configure.subcommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.panda.jda.command.EventEntities;
import org.panda.jda.command.SubcommandObjectV2;
import org.rsa.logic.constants.GuildConfigurationConstant;
import org.rsa.logic.data.managers.GuildConfigurationManager;

import java.util.Objects;

public class ConfigureRoles extends SubcommandObjectV2 {
    public ConfigureRoles() {
        super("roles", "Configure server roles.");
        // TODO: Introduce helper method for mapping `GuildConfigurationConstant` to a choice list.
        addOptions(
            new OptionData(OptionType.STRING, "option", "Specify role to configure", true)
                .addChoice(GuildConfigurationConstant.MODERATOR_ROLE.getLocalization(), GuildConfigurationConstant.MODERATOR_ROLE.getKey())
                .addChoice(GuildConfigurationConstant.HELPER_ROLE.getLocalization(), GuildConfigurationConstant.HELPER_ROLE.getKey())
                .addChoice(GuildConfigurationConstant.RESOLVER_ROLE.getLocalization(), GuildConfigurationConstant.RESOLVER_ROLE.getKey()),
            new OptionData(OptionType.ROLE, "value", "Specify new role", true));
    }

    @Override
    public void processSlashCommandInteraction(EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();
        String option = event.getOption("option", OptionMapping::getAsString);
        // TODO: Evaluate if requireNonNull can throw NPE here. Discord prevents events passing null if `isRequired`.
        String value = Objects.requireNonNull(event.getOption("value", OptionMapping::getAsRole)).getId();
        event.reply(GuildConfigurationManager.processUpdate(entities.getGuild(), option, value)).setEphemeral(true).queue();
    }
}
