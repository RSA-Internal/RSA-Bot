package org.rsa.listeners;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.Commands;

import java.util.Objects;

public class AutoCompleteListener extends ListenerAdapter {
    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        super.onCommandAutoCompleteInteraction(event);
        if (event.getGuild() == null) {
            return;
        }

        CommandObjectV2 commandObjectV2 = Commands.getCommandV2(event.getName());
        if (Objects.nonNull(commandObjectV2) && commandObjectV2.isAutocomplete()) {
            commandObjectV2.onCommandAutoCompleteInteraction(event);
        }
    }
}
