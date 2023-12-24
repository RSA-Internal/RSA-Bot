package org.rsa.command;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.*;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.rsa.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public abstract class CommandObject extends ListenerAdapter {

    private final String name;
    private final String description;
    private final List<OptionData> optionDataList;
    private final List<SubcommandData> subcommandDataList;
    protected final Map<String, Consumer<SlashCommandInteractionEvent>> subcommandHandlers = new HashMap<>();
    private boolean isAutocomplete = false;

    public CommandObject(String name, String description) {
        this(name, description, Collections.emptyList(), Collections.emptyList());
    }

    public CommandObject(String name, String description, List<OptionData> optionDataList, List<SubcommandData> subcommandDataList) {
        this.name = name;
        this.description = description;
        this.optionDataList = new ArrayList<>(optionDataList);
        this.subcommandDataList = new ArrayList<>(subcommandDataList);
    }

    public void addOptionData(OptionData optionData) {
        optionDataList.add(optionData);
    }

    public void addSubcommand(SubcommandData subcommandData, Consumer<SlashCommandInteractionEvent> handler) {
        subcommandDataList.add(subcommandData);
        subcommandHandlers.put(subcommandData.getName(), handler);
    }

    public void setIsAutocomplete() {
        isAutocomplete = true;
    }

    public List<OptionData> getOptionDataList() {
        return new ArrayList<>(optionDataList);
    }
    public List<SubcommandData> getSubcommandList() { return new ArrayList<>(subcommandDataList); }

    public SlashCommandData slashCommandImplementation() {
        SlashCommandData slashCommandData = Commands.slash(name, description);

        if (!subcommandDataList.isEmpty()) {
            slashCommandData.addSubcommands(subcommandDataList);
        } else {
            slashCommandData.addOptions(optionDataList);
        }

        return slashCommandData;
    }

    public abstract void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) throws ValidationException;
    public void handleSubCommand(@NotNull SlashCommandInteractionEvent event, String subcommandName) {
        Consumer<SlashCommandInteractionEvent> handler = subcommandHandlers.get(subcommandName);
        if (handler != null) {
            handler.accept(event);
        } else {
            event.reply("Unknown subcommand.").setEphemeral(true).queue();
        }
    }
}
