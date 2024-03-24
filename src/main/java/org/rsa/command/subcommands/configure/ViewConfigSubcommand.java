package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;
import org.rsa.translator.ConfigurationTranslator;

public class ViewConfigSubcommand extends SubcommandObject {

    public ViewConfigSubcommand() {
        super("view", "View configuration for current guild.");
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event
                .reply("This command can only be used in a Server.")
                .setEphemeral(true)
                .queue();
            return;
        }
        GuildConfiguration configuration = GuildConfigurationManager.fetch(guild.getId());
        Member requester = event.getMember();
        if (requester == null) {
            event
                .reply("This command can only be used in a Server.")
                .setEphemeral(true)
                .queue();
            return;
        }

        event.replyEmbeds(ConfigurationTranslator.getConfigurationAsEmbed(guild, configuration, requester)).setEphemeral(true).queue();
    }
}
