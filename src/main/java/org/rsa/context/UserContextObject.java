package org.rsa.context;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.rsa.exception.ValidationException;

@Getter
public abstract class UserContextObject {
    private final String name;

    public UserContextObject(String name)
    {
        this.name = name;
    }

    public CommandData getCommandData()
    {
        return Commands.user(name);
    }

    public abstract void handleInteraction(UserContextInteractionEvent event) throws ValidationException;
}
