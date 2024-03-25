package org.rsa.command.subcommands.adventure;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.rsa.adventure.model.*;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;
import org.rsa.util.HelperUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdventureTravelSubcommand extends SubcommandObject {

    public AdventureTravelSubcommand() {
        super("travel", "Travel to a new zone");
        addOption(OptionType.INTEGER, "zone", "Zone to travel to", true, true);
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event, Guild guild) {
        Member requester = event.getMember();
        if (requester == null) {
            event
                .reply("Something went wrong, please try again.")
                .setEphemeral(true)
                .queue();
            return;
        }
        UserAdventureProfile adventureProfile = UserAdventureProfileManager.fetch(guild.getId(), requester.getId());
        Integer zoneId = event.getOption("zone", 0, OptionMapping::getAsInt);
        Zone zone = Zone.getById(zoneId);

        if (Zone.START_TOWN.equals(zone)) {
            event.reply("You've returned to " + zone.getName() + ".").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Location: " + zone.getName());
        builder.setAuthor(requester.getEffectiveName());
        builder.setColor(HelperUtil.getRandomColor());
        builder.setThumbnail(requester.getEffectiveAvatarUrl());

        List<Activity> activities = zone.getActivities();
        List<ItemComponent> components = new ArrayList<>();

        for (Activity activity : activities) {
            ActivityResponse canPerform = activity.userCanPerformActivity(adventureProfile);
            String label = activity.getName();
            if (!canPerform.isResult()) {
                label = activity.getName() + " [" + canPerform.getResponse() + "]";
            }

            Button button;

            if (activity.equals(Activity.LEAVE)) {
                button = Button.primary(activity.getId().toString(), activity.getName());
            } else {
                Map<Skill, Integer> requiredSkillSetMap = activity.getRequiredSkillSet();
                List<Item> requiredItemsList = activity.getRequiredItems();
                Integer experienceBound = activity.getExperienceGainBound();
                Map<Item, ItemDrop> possibleItemsMap = activity.getPossibleItems();

                String requiredLevels = requiredSkillSetMap
                    .keySet().stream()
                    .filter(skill -> requiredSkillSetMap.get(skill) > 0)
                    .map(skill -> " - " + skill.getName() + ": " + requiredSkillSetMap.get(skill))
                    .collect(Collectors.joining("\n"));
                String requiredItems = requiredItemsList.stream()
                    .map(item -> " - " + item.getName())
                    .collect(Collectors.joining("\n"));
                String possibleItems = possibleItemsMap
                    .keySet().stream()
                    .map(item -> {
                        ItemDrop itemDrop = possibleItemsMap.get(item);
                        StringBuilder range = new StringBuilder();
                        range.append("- 1");
                        if (itemDrop.dropMax() > 1) {
                            range.append(" - ");
                            range.append(itemDrop.dropMax());
                        }
                        range.append(" ");
                        range.append(item.getName());
                        range.append(" (");
                        range.append(itemDrop.dropChance());
                        range.append("%)");

                        return range.toString();
                    })
                    .collect(Collectors.joining("\n"));

                StringBuilder requiredDisplay = getStringBuilder(requiredLevels, requiredItems, experienceBound, possibleItems);

                builder.addField(activity.getName(), requiredDisplay.toString(), true);

                button = Button
                    .success(activity.getId().toString(), label)
                    .withDisabled(!canPerform.isResult());
            }

            components.add(button);
        }

        event.replyEmbeds(builder.build()).addActionRow(components).queue();
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
    private static StringBuilder getStringBuilder(String requiredLevels, String requiredItems, Integer experienceBound, String possibleItems) {
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
