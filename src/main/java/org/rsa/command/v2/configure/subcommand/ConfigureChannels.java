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

public class ConfigureChannels extends SubcommandObjectV2 {

    public ConfigureChannels() {
        super("channels", "Configure server channels.");
        // TODO: Introduce helper method for mapping `GuildConfigurationConstant` to a choice list.
        addOptions(
            new OptionData(OptionType.STRING, "option", "Specify a channel type to configure.", true)
                .addChoice(GuildConfigurationConstant.HELP_CHANNEL.getLocalization(), GuildConfigurationConstant.HELP_CHANNEL.getKey())
                .addChoice(GuildConfigurationConstant.POLL_CHANNEL.getLocalization(), GuildConfigurationConstant.POLL_CHANNEL.getKey()),
            new OptionData(OptionType.CHANNEL, "value", "Specify the channel", true)
        );
    }

    @Override
    public void processSlashCommandInteraction(EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();
        String option = event.getOption("option", OptionMapping::getAsString);
        // TODO: Evaluate if requireNonNull can throw NPE here. Discord prevents events passing null if `isRequired`.
        String value = Objects.requireNonNull(event.getOption("value", OptionMapping::getAsChannel)).getId();
        event.reply(GuildConfigurationManager.processUpdate(entities.getGuild(), option, value)).setEphemeral(true).queue();
    }
}
