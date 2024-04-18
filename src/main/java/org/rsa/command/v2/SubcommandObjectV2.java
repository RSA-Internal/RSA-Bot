package org.rsa.command.v2;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class SubcommandObjectV2 extends SubcommandData {

    public SubcommandObjectV2(String name, String description) {
        super(name, description);
    }

    public abstract void processAutoCompleteInteraction(EventEntities<CommandAutoCompleteInteractionEvent> entities);

    public abstract void processSlashCommandInteraction(EventEntities<SlashCommandInteractionEvent> entities);
}
