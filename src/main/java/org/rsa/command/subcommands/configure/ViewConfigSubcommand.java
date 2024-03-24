package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.constants.GuildConfigurationConstant;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;
import org.rsa.translator.ConfigurationTranslator;

public class ViewConfigSubcommand extends SubcommandObject {

    public ViewConfigSubcommand() {
        super("view", "View configuration for current guild.");
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
    public void handleSubcommand(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild) {
        GuildConfiguration configuration = GuildConfigurationManager.fetch(guild.getId());
        Member requester = event.getMember();
        if (requester == null) {
            event
                .reply("This command can only be used in a Server.")
                .setEphemeral(true)
                .queue();
            return;
        }

        String list = event.getOption("section", OptionMapping::getAsString);
        MessageEmbed response;
        if (list != null) {
            list = list.replaceAll("_", " ");
            System.out.println("Rendering configuration for list: " + list);
            response = ConfigurationTranslator.getConfigurationListAsEmbed(guild, configuration, requester, list);
        } else {
            response = ConfigurationTranslator.getConfigurationAsEmbed(guild, configuration, requester);
        }

        event.replyEmbeds(response).setEphemeral(true).queue();
    }
}
