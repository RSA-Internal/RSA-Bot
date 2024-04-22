package org.rsa.command.v2.reputation.subcommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.rsa.command.v2.EventEntities;
import org.rsa.command.v2.SubcommandObjectV2;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.UserReputation;
import org.rsa.translator.ReputationTranslator;

public class ReputationView extends SubcommandObjectV2 {

    public ReputationView() {
        super("view", "View a users reputation");
        addOption(OptionType.USER, "user", "View a specific users reputation (default: yourself)");
    }

    @Override
    public void processSlashCommandInteraction(EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();
        Guild guild = entities.getGuild();
        Member requester = entities.getRequester();
        Member otherUser = event.getOption("user", requester, OptionMapping::getAsMember);
        UserReputation userReputation = ReputationManager.fetch(guild.getId(), otherUser.getId());
        event
            .replyEmbeds(ReputationTranslator.getReputationAsEmbed(guild, userReputation, requester, otherUser))
            .setEphemeral(true)
            .queue();
    }
}
