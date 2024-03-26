package org.rsa.translator;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.rsa.adventure.model.Activity;
import org.rsa.adventure.model.ActivityResponse;
import org.rsa.entity.adventure.ActivityEntity;
import org.rsa.entity.adventure.ItemEntity;
import org.rsa.entity.adventure.SkillEntity;
import org.rsa.entity.adventure.ZoneEntity;
import org.rsa.logic.data.models.UserAdventureProfile;
import org.rsa.util.HelperUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdventureTravelTranslator {

    public static EmbedBuilder getTravelEmbedBuilder(Member requester, ZoneEntity zone) {
        long startTime = System.currentTimeMillis();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Location: " + zone.getName());
        builder.setAuthor(requester.getEffectiveName());
        builder.setColor(HelperUtil.getRandomColor());
        builder.setThumbnail(requester.getEffectiveAvatarUrl());

        for (ActivityEntity activity : zone.getActivities()) {
            if (!activity.getId().equals(Activity.LEAVE.getId())) {
                List<SkillEntity> requiredSkills = activity.getRequiredSkillSet();
                List<ItemEntity> requiredItemsList = activity.getRequiredItems();
                Integer experienceBound = activity.getExperienceGainBound();
                List<ItemEntity> possibleItemsMap = activity.getPossibleItems();

                String requiredLevels = requiredSkills.stream()
                    .filter(skill -> skill.getLevel() > 0)
                    .map(skill -> " - " + skill.getName() + ": " + skill.getLevel())
                    .collect(Collectors.joining("\n"));
                String requiredItems = requiredItemsList.stream()
                    .map(item -> " - " + item.getName())
                    .collect(Collectors.joining("\n"));
                String possibleItems = ActivityEntity.getPossibleItemsAsString(activity, false, false);

                StringBuilder requiredDisplay = getTravelStringBuilder(requiredLevels, requiredItems, experienceBound, possibleItems);

                builder.addField(activity.getName(), requiredDisplay.toString(), true);
                builder.setFooter(requester.getId());
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time of 'getTravelEmbedBuilder': " + (endTime-startTime) + "ms");
        return builder;
    }

    public static List<ItemComponent> getTravelComponents(UserAdventureProfile adventureProfile, ZoneEntity zone) {
        List<ItemComponent> components = new ArrayList<>();

        for (ActivityEntity activity : zone.getActivities()) {
            ActivityResponse canPerform = activity.userCanPerformActivity(adventureProfile);
            String label = activity.getName();
            if (!canPerform.isResult()) {
                label = activity.getName() + " [" + canPerform.getResponse() + "]";
            }

            Button button;

            if (activity.getId().equals(Activity.LEAVE.getId())) {
                button = Button.primary("travel_" + activity.getId(), activity.getName());
            } else {
                button = Button
                    .success("travel_" + activity.getId(), label)
                    .withDisabled(!canPerform.isResult());
            }

            components.add(button);
        }

        return components;
    }

    private static void appendElement(StringBuilder builder, String header, String element) {
        builder.append("**");
        builder.append(header);
        builder.append("**:\n");
        if (element.isEmpty()) {
            builder.append(" - None");
        } else {
            builder.append(element);
        }
        builder.append("\n");
    }

    @NotNull
    public static StringBuilder getTravelStringBuilder(String requiredLevels, String requiredItems, Integer experienceBound, String possibleItems) {
        StringBuilder requiredDisplay = new StringBuilder();

        appendElement(requiredDisplay, "Required Levels", requiredLevels);
        appendElement(requiredDisplay, "Required Items", requiredItems);
        requiredDisplay.append("\n~~---------------------~~\n**Experience Gain**:\n");
        requiredDisplay.append("- ");
        if (experienceBound > 1) {
            requiredDisplay.append("1 - ");
            requiredDisplay.append(experienceBound);
            requiredDisplay.append(" xp");
        } else {
            requiredDisplay.append("None");
        }
        requiredDisplay.append("\n");
        appendElement(requiredDisplay, "Possible Items", possibleItems);
        return requiredDisplay;
    }
}
