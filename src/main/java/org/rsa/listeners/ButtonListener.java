package org.rsa.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

import static org.rsa.translator.AdventureProfileTranslator.getAdventureProfileAsEmbed;
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
            .setComponents(ActionRow.of(
                Button.success("travel_select", "Travel"),
                Button.primary("view_profile", "Profile")
            ))
            .queue();

        TravelSummaryManager.clearTravelSummary(requester.getId());
    }

    private void travelToZone(ButtonInteractionEvent event, Member requester, UserAdventureProfile adventureProfile, Zone zone) {
        EmbedBuilder builder = getTravelEmbedBuilder(requester, zone);
        List<ItemComponent> components = getTravelComponents(adventureProfile, zone);
        event
            .editMessage(MessageEditData.fromEmbeds(builder.build()))
            .setComponents(ActionRow.of(components))
            .queue();
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

        UserAdventureProfile adventureProfile = UserAdventureProfileManager.fetch(guild.getId(), requester.getId());

        if (componentId.equals("travel_0")) {
            travelToTown(event, guild, requester);
        } else if(componentId.equals("view_profile")) {
            MessageEmbed profileEmbed = getAdventureProfileAsEmbed(guild, adventureProfile, requester, requester);
            event
                .editMessage(MessageEditData.fromEmbeds(profileEmbed))
                .setComponents(ActionRow.of(Button.success("travel_select", "Travel")))
                .queue();
        } else if (componentId.equals("travel_select")) {
            List<ActionRow> actionRows = new ArrayList<>();
            List<Integer> unlockedZonesId = adventureProfile.getUnlockedZones();

            List<ItemComponent> components = new ArrayList<>();
            for (Integer zoneId : unlockedZonesId) {
                if (Zone.START_TOWN.getId().equals(zoneId)) continue;
                components.add(Button.success("zone_" + zoneId, Zone.getById(zoneId).getName()));

                if (components.size()==5) {
                    actionRows.add(ActionRow.of(new ArrayList<>(components)));
                    components = new ArrayList<>();
                }
            }
            actionRows.add(ActionRow.of(new ArrayList<>(components)));

            event
                .editMessage(MessageEditData.fromEmbeds(
                    new EmbedBuilder()
                        .setTitle("Travel where")
                        .setAuthor(requester.getEffectiveName())
                        .setColor(HelperUtil.getRandomColor())
                        .setThumbnail(requester.getEffectiveAvatarUrl())
                        .build()
                ))
                .setComponents(actionRows)
                .queue();
        } else {
            int idInComponent = -1;
            if (componentId.contains("_")) {
                idInComponent = Integer.parseInt(componentId.substring(componentId.indexOf("_") + 1));
            }

            if (componentId.contains("travel")) {
                // Parse Activity and perform
                Activity activity = Activity.getById(idInComponent);
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
                    Zone currentZone = Zone.getById(currentZoneId);
                    travelToZone(event, requester, adventureProfile, currentZone);
                }
            } else if(componentId.contains("zone")) {
                Zone zone = Zone.getById(idInComponent);
                travelToZone(event, requester, adventureProfile, zone);
            }
        }
    }
}
