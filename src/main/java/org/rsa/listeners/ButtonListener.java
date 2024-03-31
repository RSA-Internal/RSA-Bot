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
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.rsa.entity.recipe.RecipeEntity;
import org.rsa.register.adventure.EntityManagerRegister;
import org.rsa.manager.adventure.UserZoneManager;
import org.rsa.model.adventure.entity.Activity;
import org.rsa.model.adventure.response.ActivityPerformResponse;
import org.rsa.model.adventure.response.ActivityResponse;
import org.rsa.model.adventure.entity.Zone;
import org.rsa.entity.adventure.ActivityEntity;
import org.rsa.entity.adventure.ZoneEntity;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.rsa.manager.adventure.RecipeBookManager.generateActionRows;
import static org.rsa.manager.adventure.UserZoneManager.travelToTown;
import static org.rsa.manager.adventure.UserZoneManager.travelToZone;
import static org.rsa.translator.AdventureProfileTranslator.getAdventureProfileAsEmbed;
import static org.rsa.util.EmbedBuilderUtil.*;

public class ButtonListener extends ListenerAdapter {

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

        switch (componentId) {
            case "travel_0" -> travelToTown(event, guild, requester);
            case "view_profile" -> {
                MessageEmbed profileEmbed = getAdventureProfileAsEmbed(guild, adventureProfile, requester, requester);
                event
                    .editMessage(MessageEditData.fromEmbeds(profileEmbed))
                    .setComponents(ActionRow.of(Button.success("travel_select", "Travel")))
                    .queue();
            }
            case "travel_select" -> {
                List<ActionRow> actionRows = new ArrayList<>();
                List<Integer> unlockedZonesId = adventureProfile.getUnlockedZones();
                List<ItemComponent> components = new ArrayList<>();
                for (Integer zoneId : unlockedZonesId) {
                    if (Zone.START_TOWN.getId().equals(zoneId)) continue;
                    ZoneEntity zone = EntityManagerRegister.zoneManager.getEntityById(zoneId);
                    components.add(Button.success("zone_" + zoneId, zone.getName()));

                    if (components.size() == 5) {
                        actionRows.add(ActionRow.of(new ArrayList<>(components)));
                        components = new ArrayList<>();
                    }
                }
                actionRows.add(ActionRow.of(new ArrayList<>(components)));
                event
                    .editMessage(MessageEditData.fromEmbeds(
                        getEmbedBuilderTemplate(guild, requester, "Travel where?").build()
                    ))
                    .setComponents(actionRows)
                    .queue();
            }
            default -> {
                int idInComponent = -1;
                if (componentId.contains("_")) {
                    idInComponent = Integer.parseInt(componentId.substring(componentId.indexOf("_") + 1));
                }
                if (componentId.contains("travel")) {
                    // Parse Activity and perform
                    ActivityEntity activity = EntityManagerRegister.activityManager.getEntityById(idInComponent);
                    ActivityResponse validationResponse = activity.userCanPerformActivity(adventureProfile, false);

                    ActivityPerformResponse performResponse = null;

                    if (!validationResponse.isResult()) {
                        if (validationResponse.getResponse().contains("You must wait")) {
                            performResponse = new ActivityPerformResponse();
                            performResponse.addMessage(validationResponse.getResponse());
                        } else {
                            travelToTown(event, guild, requester);
                            return;
                        }
                    }

                    if (performResponse == null) {
                        performResponse = activity.perform(adventureProfile);
                    }

                    EmbedBuilder builder = getActivitySummaryEmbedBuilder(guild, requester, adventureProfile, activity.getName() + " results.", performResponse);
                    UserAdventureProfileManager.update(adventureProfile);

                    MessageEmbed existingEmbed = event.getMessage().getEmbeds().get(0);
                    if (existingEmbed != null) {
                        String ownerId = Objects.requireNonNull(existingEmbed.getFooter()).getText();
                        if (requester.getId().equals(ownerId)) {
                            event
                                .editMessage(MessageEditData.fromEmbeds(builder.build()))
                                .setComponents(ActionRow.of(
                                    Button.success("travel_" + activity.getId(), activity.getName() + " again"),
                                    Button.success("return_" + activity.getId(), "Go back"),
                                    Button.primary("travel_" + Activity.LEAVE.getId(), "Leave")
                                ))
                                .queue();
                        } else {
                            event
                                .reply(MessageCreateData.fromEmbeds(builder.build()))
                                .setComponents(ActionRow.of(
                                    Button.success("travel_" + activity.getId(), activity.getName() + " again"),
                                    Button.success("return_" + activity.getId(), "Go back"),
                                    Button.primary("travel_" + Activity.LEAVE.getId(), "Leave")
                                ))
                                .queue();
                        }
                    }
                } else if (componentId.contains("return")) {
                    int currentZoneId = UserZoneManager.getUserCurrentZone(requester.getId());
                    if (currentZoneId == Zone.START_TOWN.getId()) {
                        travelToTown(event, guild, requester);
                    } else {
                        ZoneEntity currentZone = EntityManagerRegister.zoneManager.getEntityById(currentZoneId);
                        travelToZone(event, requester, adventureProfile, currentZone);
                    }
                } else if (componentId.contains("zone")) {
                    ZoneEntity zone = EntityManagerRegister.zoneManager.getEntityById(idInComponent) ;
                    travelToZone(event, requester, adventureProfile, zone);
                } else if (componentId.contains("craft")) {
                    RecipeEntity recipe = EntityManagerRegister.recipeManager.getEntityById(idInComponent);
                    if (recipe.performCraft(adventureProfile)) {
                        event
                            .reply("Successfully crafted " + recipe.getName() + ".")
                            .setEmbeds(getRecipeBookEmbedBuilder(requester).build())
                            .setComponents(generateActionRows(requester))
                            .setEphemeral(true)
                            .queue();
                    } else {
                        event
                            .reply("Failed to craft " + recipe.getName() + ".")
                            .setEmbeds(getRecipeBookEmbedBuilder(requester).build())
                            .setComponents(generateActionRows(requester))
                            .setEphemeral(true)
                            .queue();
                    }
                }
            }
        }
    }
}
