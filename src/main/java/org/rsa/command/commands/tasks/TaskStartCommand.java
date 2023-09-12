package org.rsa.command.commands.tasks;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.CommandObject;
import org.rsa.exception.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.rsa.cache.TaskCache.guildTaskListCache;

public class TaskStartCommand extends CommandObject {

    public TaskStartCommand() {
        super("task-start", "Start a new task.");
        addOptionData(new OptionData(OptionType.STRING, "task-name",
                "The name of the task to start.", true, true));
        setIsAutocomplete();
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        Guild guild = event.getGuild();

        if (null == guild) {
            event.replyChoices(Collections.emptyList()).queue();
            return;
        }

        Set<String> guildTaskNameList;
        try {
            guildTaskNameList = guildTaskListCache.get(guild.getId());
        } catch (ExecutionException e) {
            event.replyChoices(Collections.emptyList()).queue();
            return;
        }
        String focusedOptionValue = event.getFocusedOption().getValue();
        List<Command.Choice> filteredTaskNameList = guildTaskNameList.stream()
                .filter(e -> e.startsWith(focusedOptionValue))
                .map(e -> new Command.Choice(e, e))
                .limit(25)
                .toList();
        event.replyChoices(filteredTaskNameList).queue();
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) throws ValidationException {
        throw new ValidationException("Command not yet implemented");
    }
}
