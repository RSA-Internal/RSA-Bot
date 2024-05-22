package org.rsa.command.backup.subcommand;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.panda.jda.command.EventEntities;
import org.panda.jda.command.SubcommandObjectV2;

import static org.rsa.command.backup.BackupConstants.SUPPORTED_CATEGORIES;

public class BrowseBackupSubcommand extends SubcommandObjectV2 {

    public BrowseBackupSubcommand() {
        super("browse", "View pagination of the current backup.");
        addOptions(
            new OptionData(
                OptionType.STRING, "category", "The category to view a resource in.", true, true));
    }

    @Override
    public void processAutoCompleteInteraction(EventEntities<CommandAutoCompleteInteractionEvent> entities) {
        entities
            .getEvent()
            .replyChoices(
                SUPPORTED_CATEGORIES.stream()
                    .map(category -> new Command.Choice(category, category))
                    .toList())
            .queue();
    }
}
