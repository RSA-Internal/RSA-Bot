package org.rsa.command;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.rsa.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class CommandObject extends ListenerAdapter {

    private final String name;
    private final String description;
    private final List<OptionData> optionDataList;
    private final List<SubcommandObject> subcommandList;
    private boolean isAutocomplete = false;
    private boolean isGuildOnly = false;

    public CommandObject(String name, String description) {
        this(name, description, Collections.emptyList(), Collections.emptyList());
    }

    public CommandObject(String name, String description, List<OptionData> optionDataList)
    {
        this(name, description, optionDataList, Collections.emptyList());
    }

    public CommandObject(String name, String description, List<OptionData> optionDataList, List<SubcommandObject> subcommandList) {
        this.name = name;
        this.description = description;
        this.optionDataList = new ArrayList<>(optionDataList);
        this.subcommandList = new ArrayList<>(subcommandList);
    }

    public void addOptionData(OptionData optionData) {
        optionDataList.add(optionData);
    }

    public void addSubcommand(SubcommandObject subcommandObject) { subcommandList.add(subcommandObject); }

    public void setIsAutocomplete() {
        isAutocomplete = true;
    }

    public void setIsGuildOnly() { isGuildOnly = true; }

    public List<OptionData> getOptionDataList() {
        return new ArrayList<>(optionDataList);
    }

    public SlashCommandData slashCommandImplementation() {
        return Commands.slash(name, description).addOptions(optionDataList).addSubcommands(subcommandList).setGuildOnly(isGuildOnly);
    }

    public abstract void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) throws ValidationException;
}
