package org.rsa.command;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.rsa.exception.ValidationException;
import org.rsa.logic.data.managers.GuildConfigurationManager;

import java.util.*;

@Getter
public abstract class CommandObject extends ListenerAdapter {

    private final String name;
    private final String description;
    private final List<OptionData> optionDataList;
    private final List<SubcommandPassthroughObject> subcommandList;
    private boolean isAutocomplete = false;
    private boolean isGuildOnly = false;

    public CommandObject(String name, String description) {
        this(name, description, Collections.emptyList(), Collections.emptyList());
    }

    public CommandObject(String name, String description, List<OptionData> optionDataList)
    {
        this(name, description, optionDataList, Collections.emptyList());
    }

    public CommandObject(String name, String description, List<OptionData> optionDataList, List<SubcommandPassthroughObject> subcommandList) {
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

    public void setIsGuildOnly() {
        isGuildOnly = true;
    }

    public List<OptionData> getOptionDataList() {
        return new ArrayList<>(optionDataList);
    }

    public SlashCommandData slashCommandImplementation() {
        return Commands.slash(name, description).addOptions(optionDataList).addSubcommands(subcommandList).setGuildOnly(isGuildOnly);
    }

    public abstract void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) throws ValidationException;

    protected void processSubcommand(SlashCommandInteractionEvent event, SubcommandPassthroughObject[] subcommandObjects, String subCommandName) {
        if (subCommandName != null) {
            Guild guild = event.getGuild();
            if (null == guild && isGuildOnly()) {
                event.reply("This command can only be used in a Server.").setEphemeral(true).queue();
                return;
            }

            Optional<SubcommandPassthroughObject> optionalSubcommand = Arrays.stream(subcommandObjects).filter(s -> s.getName().equals(subCommandName)).findFirst();
            optionalSubcommand.ifPresent(subcommandObject -> {
                if (subcommandObject instanceof SubcommandObject) {
                    ((SubcommandObject) subcommandObject).handleSubcommand(event, guild);
                    return;
                }

                OptionValueWrapper<String, String> optionValueWrapper = subcommandObject.processEvent(event);
                String response;
                if (!optionValueWrapper.isValid()) {
                    response = "An error occurred, please try again later.";
                } else {
                    if (guild == null) {
                        response = "An error occurred, please try again later.";
                    } else {
                        // TODO: Support other than String
                        response = GuildConfigurationManager.processUpdate(guild, optionValueWrapper.getOption(), optionValueWrapper.getValue());
                    }
                }

                event
                    .reply(response)
                    .setEphemeral(true)
                    .queue();
            });
        }
    }
}
