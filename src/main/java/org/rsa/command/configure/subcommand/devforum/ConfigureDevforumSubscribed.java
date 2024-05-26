package org.rsa.command.configure.subcommand.devforum;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.panda.jda.command.EventEntities;
import org.panda.jda.command.SubcommandObjectV2;
import org.rsa.logic.data.managers.DevforumUpdatesManager;
import org.rsa.logic.data.models.DevforumUpdates;
import org.rsa.net.apis.ApiFactory;
import org.rsa.net.apis.discourse.DiscourseApi;
import org.rsa.net.apis.discourse.domain.Category;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.interactions.commands.build.CommandData.MAX_OPTIONS;

public class ConfigureDevforumSubscribed extends SubcommandObjectV2 {
    private final DiscourseApi discourseApi;
    private static final long CACHE_TTL = 5; // Minutes
    private final Cache<String, List<Integer>> enabledTopicsCache;

    public ConfigureDevforumSubscribed() {
        super("subscribed", "Configure subscribed categories.");

        addOptions(new OptionData(OptionType.INTEGER, "category", "Subscribed categories", true, true));
        discourseApi = ApiFactory.getDiscourseApi();
        this.enabledTopicsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(CACHE_TTL, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void processSlashCommandInteraction(EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();
        String guildId = event.getGuild().getId();

        Integer categoryId = event.getOption("category", OptionMapping::getAsInt);
        try {
            String categoryName = discourseApi.getLatestCategoryInformation().get(String.valueOf(categoryId)).name();
            DevforumUpdatesManager devforumUpdatesManager = new DevforumUpdatesManager();
            devforumUpdatesManager.updateSubscriptionStatus(guildId, categoryId);
            enabledTopicsCache.invalidate(guildId);
            event.reply(String.format("Subscribed to `%s (ID: %d)`", categoryName, categoryId)).setEphemeral(true).queue();
        } catch (IOException e) {
            System.err.println("Failed to update subscribed categories: " + e.getMessage());
            event.reply("Failed to update subscribed categories.").setEphemeral(true).queue();
        }
    }

    @Override
    public void processAutoCompleteInteraction(EventEntities<CommandAutoCompleteInteractionEvent> entities) {
        CommandAutoCompleteInteractionEvent event = entities.getEvent();
        AutoCompleteQuery focusedOption = event.getFocusedOption();
        String targetCategory = focusedOption.getValue().toLowerCase();
        String guildId = event.getGuild().getId();

        List<Command.Choice> options = Collections.emptyList();

        try {
            List<Integer> enabledTopics = enabledTopicsCache.get(guildId, () -> fetchCategoriesEnabled(guildId));

            options = discourseApi.getLatestCategoryInformation().values().parallelStream()
                    .sorted(Comparator.comparing(Category::name))
                    .filter(category -> category.name().toLowerCase().startsWith(targetCategory))
                    .limit(MAX_OPTIONS)
                    .map(category -> {
                        if (enabledTopics.contains(category.id())) {
                            return new Command.Choice("✅ " + category.name(), category.id());
                        }

                        return new Command.Choice(category.name(), category.id());
                    })
                    .collect(Collectors.toList());
        } catch (IOException | ExecutionException e) {
            System.err.println(e.getMessage());
        }

        event.replyChoices(options).queue();
    }

    private List<Integer> fetchCategoriesEnabled(String guildId) {
        DevforumUpdates devforumUpdates = DevforumUpdatesManager.fetch(guildId);
        List<Integer> enabledTopics = devforumUpdates.getEnabled_topics();
        enabledTopicsCache.put(guildId, enabledTopics);
        return enabledTopics;
    }
}
