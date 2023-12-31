package org.rsa.command;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.rsa.exception.ValidationException;

@Getter
public abstract class MessageContextObject {
    private final String name;

    public MessageContextObject(String name)
    {
        this.name = name;
    }

    public CommandData getCommandData()
    {
        return Commands.message(name);
    }

    public abstract void handleInteraction(MessageContextInteractionEvent event) throws ValidationException;
}
