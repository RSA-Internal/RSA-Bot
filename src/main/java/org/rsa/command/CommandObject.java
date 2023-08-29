package org.rsa.command;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class CommandObject extends ListenerAdapter {

    private final String name;
    private final String description;
    private final List<OptionData> optionDataList;
    private boolean isAutocomplete = false;

    public CommandObject(String name, String description) {
        this(name, description, Collections.emptyList());
    }

    public CommandObject(String name, String description, List<OptionData> optionDataList) {
        this.name = name;
        this.description = description;
        this.optionDataList = new ArrayList<>(optionDataList);
    }

    public void addOptionData(OptionData optionData) {
        optionDataList.add(optionData);
    }

    public void setIsAutocomplete() {
        isAutocomplete = true;
    }

    public List<OptionData> getOptionDataList() {
        return new ArrayList<>(optionDataList);
    }

    public SlashCommandData slashCommandImplementation() {
        return Commands.slash(name, description).addOptions(optionDataList);
    }

    public abstract void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event);
}
