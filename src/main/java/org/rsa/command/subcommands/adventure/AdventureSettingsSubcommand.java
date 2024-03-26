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

public class AdventureSettingsSubcommand extends SubcommandObject {

    public AdventureSettingsSubcommand() {
        super("settings", "Customize your Adventure experience!");
        addOptions(
            new OptionData(OptionType.STRING, "option", "Specify the setting to configure", true)
                .addChoice("Embed Color", "embed_color"),
            new OptionData(OptionType.STRING, "value", "The value for the setting.", true)
        );
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event, Guild guild) {
        Member requester = event.getMember();
        if (requester == null) {
            event.reply("Something went wrong, please try again.").setEphemeral(true).queue();
            return;
        }

        String option = event.getOption("option", OptionMapping::getAsString);
        String value = event.getOption("value", OptionMapping::getAsString);
        if (option == null || option.isEmpty() || value == null || value.isEmpty()) {
            event.reply("Something went wrong, please try again.").setEphemeral(true).queue();
            return;
        }

        UserAdventureProfile profile = UserAdventureProfileManager.fetch(guild.getId(), requester.getId());

        if (option.equals("embed_color")) {
            String hexCode = value.replaceAll("#", "");
            if (hexCode.matches("^([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$")) {
                profile.setColorCode(hexCode);
                UserAdventureProfileManager.update(profile);
                event.reply("Successfully updated embed color code.").setEphemeral(true).queue();
            } else {
                event.reply(value + " is not a valid hex code.").setEphemeral(true).queue();
            }
        }
    }
}
