package org.rsa.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.CommandObject;
import org.rsa.command.Commands;

public class AutoCompleteListener extends ListenerAdapter {
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        super.onCommandAutoCompleteInteraction(event);
        if (event.getGuild() == null) {
            return;
        }

        CommandObject command = Commands.getCommand(event.getName());
        if (command.isAutocomplete()) {
            command.onCommandAutoCompleteInteraction(event);
        }
    }
}
