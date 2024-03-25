package org.rsa.command.subcommands.adventure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;
import org.rsa.translator.AdventureProfileTranslator;

public class AdventureProfileSubcommand extends SubcommandObject {

    public AdventureProfileSubcommand() {
        super("profile", "View the profile of a user.");
        addOptions(new OptionData(OptionType.USER, "user", "The user to view", false));
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event, Guild guild) {
        Member requester = event.getMember();
        if (requester == null) {
            event
                .reply("Something went wrong, please try again.")
                .setEphemeral(true)
                .queue();
            return;
        }

        Member profileView = event.getOption("user", OptionMapping::getAsMember);
        if (profileView == null) {
            profileView = requester;
        }

        UserAdventureProfile adventureProfile = UserAdventureProfileManager.fetch(guild.getId(), profileView.getId());
        event
            .replyEmbeds(AdventureProfileTranslator.getAdventureProfileAsEmbed(guild, adventureProfile, requester, profileView))
            .queue();
    }
}
