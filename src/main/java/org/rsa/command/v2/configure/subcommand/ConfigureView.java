package org.rsa.command.v2.configure.subcommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.v2.EventEntities;
import org.rsa.command.v2.SubcommandObjectV2;
import org.rsa.logic.constants.GuildConfigurationConstant;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;
import org.rsa.translator.ConfigurationTranslator;

public class ConfigureView extends SubcommandObjectV2 {
    public ConfigureView() {
        super("view", "View configure for the current guild.");
        // TODO: Introduce helper method for mapping `GuildConfigurationConstant` to a choice list.
        addOptions(
            new OptionData(OptionType.STRING, "section", "Section of the config to view", false)
                .addChoice(GuildConfigurationConstant.EMOJI_LIST_KEY, GuildConfigurationConstant.EMOJI_LIST_KEY.replaceAll(" ", "_").toLowerCase())
                .addChoice(GuildConfigurationConstant.ROLE_LIST_KEY, GuildConfigurationConstant.ROLE_LIST_KEY.replaceAll(" ", "_").toLowerCase())
                .addChoice(GuildConfigurationConstant.CHANNEL_LIST_KEY, GuildConfigurationConstant.CHANNEL_LIST_KEY.replaceAll(" ", "_").toLowerCase())
                .addChoice(GuildConfigurationConstant.OPTION_LIST_KEY, GuildConfigurationConstant.OPTION_LIST_KEY.replaceAll(" ", "_").toLowerCase())
                .addChoice(GuildConfigurationConstant.REPUTATION_LIST_KEY, GuildConfigurationConstant.REPUTATION_LIST_KEY.replaceAll(" ", "_").toLowerCase())
        );
    }

    @Override
    public void processSlashCommandInteraction(EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();
        Guild guild = entities.getGuild();
        GuildConfiguration configuration = GuildConfigurationManager.fetch(guild.getId());
        Member requester = entities.getRequester();
        String list = event.getOption("section", OptionMapping::getAsString);
        MessageEmbed response = ConfigurationTranslator.getConfigurationAsEmbed(guild, configuration, requester);
        if (list != null) {
            list = list.replaceAll("_", " ");
            System.out.println("Rendering configuration for list: " + list);
            response = ConfigurationTranslator.getConfigurationListAsEmbed(guild, configuration, requester, list);
        }

        event.replyEmbeds(response).setEphemeral(true).queue();
    }
}
