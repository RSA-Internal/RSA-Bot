package org.rsa.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class SubcommandObject extends SubcommandPassthroughObject {

    public SubcommandObject(String name, String description) {
        super(name, description, null, null);
    }

    public abstract void handleSubcommand(SlashCommandInteractionEvent event, Guild guild);
}
