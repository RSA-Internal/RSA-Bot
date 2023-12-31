package org.rsa.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public abstract class SubcommandObject extends SubcommandData {
    public SubcommandObject(String name, String description)
    {
        super(name, description);
    }

    public abstract void handleSubcommand(SlashCommandInteractionEvent event);
}
