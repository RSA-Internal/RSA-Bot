package org.rsa.command.subcommands.reputation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.UserReputation;
import org.rsa.translator.ReputationTranslator;

public class ViewSubcommand extends SubcommandObject {

    public ViewSubcommand() {
        super("view", "View a users reputation.");
        addOption(OptionType.USER, "user", "View a specific users reputation (default: yourself)");
    }

    @Override
    public void handleSubcommand(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild) {
        Member requester = event.getMember();
        if (null == requester) {
            event
                .reply("Something went wrong. Please try again later.")
                .setEphemeral(true)
                .queue();
            return;
        }

        Member otherUser = event.getOption("user", requester, OptionMapping::getAsMember);
        if (null == otherUser) {
            event
                .reply("Cannot view reputation of a null member.")
                .setEphemeral(true)
                .queue();
            return;
        }

        UserReputation userReputation = ReputationManager.fetch(guild.getId(), otherUser.getId());
        event
            .replyEmbeds(ReputationTranslator.getReputationAsEmbed(guild, userReputation, requester, otherUser))
            .setEphemeral(true)
            .queue();
    }
}
