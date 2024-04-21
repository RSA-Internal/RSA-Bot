package org.rsa.command.v2.configure.subcommand;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.v2.EventEntities;
import org.rsa.command.v2.SubcommandObjectV2;
import org.rsa.logic.constants.GuildConfigurationConstant;
import org.rsa.logic.data.managers.GuildConfigurationManager;

public class ConfigureReactions extends SubcommandObjectV2 {
    public ConfigureReactions() {
        super("reactions", "Change reaction emojis");
        // TODO: Introduce helper method for mapping `GuildConfigurationConstant` to a choice list.
        addOptions(
            new OptionData(OptionType.STRING, "option", "Specify the reaction emoji to change.", true)
                .addChoice(GuildConfigurationConstant.UPVOTE_EMOJI.getLocalization(), GuildConfigurationConstant.UPVOTE_EMOJI.getKey())
                .addChoice(GuildConfigurationConstant.DOWNVOTE_EMOJI.getLocalization(), GuildConfigurationConstant.DOWNVOTE_EMOJI.getKey())
                .addChoice(GuildConfigurationConstant.MODERATE_EMOJI.getLocalization(), GuildConfigurationConstant.MODERATE_EMOJI.getKey())
                .addChoice(GuildConfigurationConstant.ACCEPT_EMOJI.getLocalization(), GuildConfigurationConstant.ACCEPT_EMOJI.getKey()),
            new OptionData(OptionType.STRING, "value", "New emoji to assign to reaction emoji", true));
    }

    @Override
    public void processSlashCommandInteraction(EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();
        String option = event.getOption("option", OptionMapping::getAsString);
        String value = event.getOption("value", OptionMapping::getAsString);
        GuildConfigurationManager.processUpdate(entities.getGuild(), option, value);
    }
}
