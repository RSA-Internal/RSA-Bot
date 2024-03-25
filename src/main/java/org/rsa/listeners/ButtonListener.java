package org.rsa.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.rsa.adventure.TravelSummaryManager;
import org.rsa.adventure.UserZoneManager;
import org.rsa.adventure.model.*;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;
import org.rsa.util.HelperUtil;

import java.util.*;
import java.util.stream.Collectors;

import static org.rsa.translator.AdventureTravelTranslator.getTravelComponents;
import static org.rsa.translator.AdventureTravelTranslator.getTravelEmbedBuilder;

public class ButtonListener extends ListenerAdapter {

    private EmbedBuilder displayActivitySummary(Member requester, UserAdventureProfile adventureProfile, String title, ActivityPerformResponse performResponse) {
        // Display results.
        EmbedBuilder builder = new EmbedBuilder()
            .setTitle(title)
            .setAuthor(requester.getEffectiveName())
            .setColor(HelperUtil.getRandomColor())
            .setThumbnail(requester.getEffectiveAvatarUrl());

        Map<Item, Integer> itemsReceived = performResponse.getItemsReceived();
        String itemReceivedDisplay = itemsReceived.keySet().stream()
            .map(item -> "- " + itemsReceived.get(item) + " " + item.getName())
            .collect(Collectors.joining("\n"));
        if (itemReceivedDisplay.isEmpty()) {
            itemReceivedDisplay = "- None";
        }
        builder.addField("Items Received", itemReceivedDisplay, false);

        Map<Skill, Integer> experienceGained = performResponse.getExperienceGained();
        String experienceGainedDisplay = experienceGained.keySet().stream()
            .map(skill -> "- " + skill.getName() + " " + experienceGained.get(skill) + " xp")
            .collect(Collectors.joining("\n"));
        if (experienceGainedDisplay.isEmpty()) {
            experienceGainedDisplay = "- None";
        }
        builder.addField("Experience Gained", experienceGainedDisplay, false);

        List<Skill> skillsLeveled = performResponse.getSkillsLeveledUp();
        if (!skillsLeveled.isEmpty()) {
            StringBuilder skillBuilder = new StringBuilder();
            Set<Skill> uniqueLevels = new HashSet<>(skillsLeveled);
            for (Skill skill : uniqueLevels) {
                int currentLevel = adventureProfile.getSkillSetLevel().get(skill.getId());
                int timesLeveled = (int) skillsLeveled.stream().filter(leveledSkill -> leveledSkill.getId().equals(skill.getId())).count();
                int previousLevel = currentLevel - timesLeveled;
                skillBuilder.append("- ");
                skillBuilder.append(skill.getName());
                skillBuilder.append(": ");
                skillBuilder.append(previousLevel);
                skillBuilder.append(" -> ");
                skillBuilder.append(currentLevel);
            }
            builder.addField("Skills Leveled Up", skillBuilder.toString(), false);
        }
        return builder;
    }

    private void travelToTown(ButtonInteractionEvent event, Guild guild, Member requester) {
        ActivityPerformResponse travelSummary = TravelSummaryManager.getUserSummary(requester.getId());
        UserAdventureProfile adventureProfile = UserAdventureProfileManager.fetch(guild.getId(), requester.getId());
        EmbedBuilder builder = displayActivitySummary(requester, adventureProfile, "Travel Summary", travelSummary);

        event
            .editMessage(MessageEditData.fromEmbeds(builder.build()))
            .setComponents(Collections.emptyList())
            .queue();

        TravelSummaryManager.clearTravelSummary(requester.getId());
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String componentId = event.getComponentId();

        Guild guild = event.getGuild();
        Member requester = event.getMember();

        if (guild == null || requester == null) {
            event.reply("Something went wrong, please try again.").setEphemeral(true).queue();
            return;
        }

        if (componentId.equals("travel_0")) {
            travelToTown(event, guild, requester);
        } else {
            UserAdventureProfile adventureProfile = UserAdventureProfileManager.fetch(guild.getId(), requester.getId());
            if (componentId.contains("travel")) {
                // Parse Activity and perform
                int activityId = Integer.parseInt(componentId.substring(componentId.indexOf("_") + 1));
                Activity activity = Activity.getById(activityId);
                ActivityPerformResponse performResponse = activity.perform(adventureProfile);

                EmbedBuilder builder = displayActivitySummary(requester, adventureProfile, activity.getName() + " results.", performResponse);
                UserAdventureProfileManager.update(adventureProfile);

                event
                    .editMessage(MessageEditData.fromEmbeds(builder.build()))
                    .setComponents(ActionRow.of(
                        Button.success("travel_" + activity.getId(), activity.getName() + " again"),
                        Button.success("return_" + activity.getId(), "Go back"),
                        Button.primary("travel_" + Activity.LEAVE.getId(), "Leave")
                    ))
                    .queue();
            } else if(componentId.contains("return")) {
                int currentZoneId = UserZoneManager.getUserCurrentZone(requester.getId());
                if (currentZoneId == Zone.START_TOWN.getId()) {
                    travelToTown(event, guild, requester);
                } else {
                    // Display adventure embed
                    Zone currentZone = Zone.getById(currentZoneId);
                    EmbedBuilder builder = getTravelEmbedBuilder(requester, currentZone);
                    List<ItemComponent> components = getTravelComponents(adventureProfile, currentZone);
                    event
                        .editMessage(MessageEditData.fromEmbeds(builder.build()))
                        .setComponents(ActionRow.of(components))
                        .queue();
                }
            }
        }
    }
}
