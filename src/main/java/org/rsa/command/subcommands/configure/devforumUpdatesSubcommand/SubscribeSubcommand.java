package org.rsa.command.subcommands.configure.devforumUpdatesSubcommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.SubcommandObject;

public class SubscribeSubcommand extends SubcommandObject {
    public SubscribeSubcommand() {
        super("subscribe", "Edit subscribed Devforum categories");
        addOptions(new OptionData(OptionType.STRING, "category", "Select a category", true, true));
    }

    public void handleSubcommand(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild) {
        event.reply("Edited subscribed category").setEphemeral(true).queue();
    }
}
