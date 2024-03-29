package org.rsa.command.subcommands.adventure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.rsa.manager.adventure.IndexManager;
import org.rsa.command.SubcommandObject;

import static org.rsa.helper.AdventureIndexHelper.getActionRowsForResponse;
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
        IndexManager.setUserPage(requester.getId(), 0);
        IndexManager.setUserSelectedEntityIndex(requester.getId(), 0);
        IndexManager.setUserSelectedEntityId(requester.getId(), -1);

        event
            .replyEmbeds(getIndexEmbedBuilder(requester).build())
            .setComponents(getActionRowsForResponse(requester))
            .setEphemeral(true)
            .queue();
    }
}
