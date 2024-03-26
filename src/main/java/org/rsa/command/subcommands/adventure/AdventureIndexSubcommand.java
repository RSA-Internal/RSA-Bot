package org.rsa.command.subcommands.adventure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.rsa.adventure.IndexManager;
import org.rsa.adventure.model.Activity;
import org.rsa.command.SubcommandObject;

import static org.rsa.adventure.IndexManager.*;
import static org.rsa.util.EmbedBuilderUtil.getIndexEmbedBuilder;

public class AdventureIndexSubcommand extends SubcommandObject {

    public AdventureIndexSubcommand() {
        super("index", "View details about a specific entity index");
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event, Guild guild) {
        Member requester = event.getMember();
        if (requester == null) {
            event.reply("Something went wrong, please try again.").setEphemeral(true).queue();
            return;
        }

        IndexManager.setUserTypeSelection(requester.getId(), "activity");

        // display embed with two select menus
        event
            .replyEmbeds(getIndexEmbedBuilder(requester, "Activity", "activity-" + Activity.HUNT.getId()).build())
            .addActionRow(getIndexSelectType(requester))
            .addActionRow(getIndexSelectEntity(requester))
            .setEphemeral(true)
            .queue();
    }
}
