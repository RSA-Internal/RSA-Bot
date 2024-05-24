package org.rsa.command.configure.subcommand;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.panda.jda.command.EventEntities;
import org.panda.jda.command.SubcommandObjectV2;
import org.rsa.beans.GuildConfiguration;
import org.rsa.managers.GuildConfigurationManager;
import org.rsa.translator.ConfigurationTranslator;
import org.rsa.util.GuildConfigurationConstant;

@Slf4j
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
            log.info("Rendering configuration for list: " + list);
            response = ConfigurationTranslator.getConfigurationListAsEmbed(guild, configuration, requester, list);
        }

        event.replyEmbeds(response).setEphemeral(true).queue();
    }
}
