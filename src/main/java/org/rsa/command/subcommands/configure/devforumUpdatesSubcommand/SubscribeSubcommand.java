package org.rsa.command.subcommands.configure.devforumUpdatesSubcommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.SubcommandObject;
import org.rsa.discourse.DiscourseAPIHelper;
import org.rsa.discourse.models.CategoryDetailsModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubscribeSubcommand extends SubcommandObject {
    public SubscribeSubcommand() {
        super("subscribe", "Edit subscribed Devforum categories");
        addOptions(new OptionData(OptionType.STRING, "category", "Select a category", true, true));
    }

    public void handleSubcommand(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild) {
        event.reply("Edited subscribed category").setEphemeral(true).queue();
    }

    private class AutoCompleteHandler extends ListenerAdapter {
        @Override
        public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
            if (event.getName().equals("subscribe") && event.getFocusedOption().getName().equals("category")) {
                String userInput = event.getFocusedOption().getValue();
                Map<String, CategoryDetailsModel> categoryDetailsModelMap = DiscourseAPIHelper.getLatestCategoriesData();
                List<Command.Choice> choices = categoryDetailsModelMap.values().stream()
                        .filter(category -> category.getName().toLowerCase().startsWith(userInput.toLowerCase()))
                        .limit(OptionData.MAX_CHOICES)
                        .map(category -> new Command.Choice(category.getName(), category.getId()))
                        .collect(Collectors.toList());

                event.replyChoices(choices).queue();
            }
        }
    }
}
