package org.rsa.command.v2.backup;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.panda.jda.command.EventEntities;
import org.panda.jda.command.SubcommandObjectV2;

import static org.rsa.command.v2.backup.BackupConstants.SUPPORTED_CATEGORIES;

public class RestoreBackupSubcommand extends SubcommandObjectV2 {

    public RestoreBackupSubcommand() {
        super("restore", "Initiate a vote to restore a resource.");
        addOptions(
            new OptionData(
                OptionType.STRING, "category", "The category to restore a resource from.", true, true),
            new OptionData(OptionType.STRING, "resource_name", "The name of the resource to restore.", true));
    }

    @Override
    public void processAutoCompleteInteraction(EventEntities<CommandAutoCompleteInteractionEvent> entities) {
        entities.getEvent().replyChoices(SUPPORTED_CATEGORIES.stream().map(category -> new Command.Choice(category, category)).toList()).queue();
    }
}
