package org.rsa.command.configure.subcommand.devforum;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.panda.jda.command.EventEntities;
import org.panda.jda.command.SubcommandObjectV2;
import org.rsa.net.apis.APIFactory;
import org.rsa.net.apis.discourse.DiscourseAPI;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.interactions.commands.build.CommandData.MAX_OPTIONS;

public class ConfigureDevforumSubscribed extends SubcommandObjectV2 {
    private final DiscourseAPI discourseAPI;

    public ConfigureDevforumSubscribed() {
        super("subscribed", "Configure subscribed categories.");

        addOptions(new OptionData(OptionType.STRING, "categories", "Subscribed categories", true, true));
        discourseAPI = APIFactory.getDiscourseAPI();
    }

    @Override
    public void processAutoCompleteInteraction(EventEntities<CommandAutoCompleteInteractionEvent> entities) {
        CommandAutoCompleteInteractionEvent event = entities.getEvent();
        AutoCompleteQuery focusedOption = event.getFocusedOption();

        List<Command.Choice> options = Collections.emptyList();
        try {
            options = discourseAPI.getLatestCategoryInformation().values().stream()
                    .filter(category -> category.name().startsWith(focusedOption.getValue().toLowerCase()))
                    .map(category -> new Command.Choice(category.name(), category.id()))
                    .limit(MAX_OPTIONS)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        event.replyChoices(options).queue();
    }
}
