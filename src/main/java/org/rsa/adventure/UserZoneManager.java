package org.rsa.adventure;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.rsa.adventure.model.ActivityPerformResponse;
import org.rsa.adventure.model.Zone;
import org.rsa.entity.adventure.ZoneEntity;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rsa.translator.AdventureTravelTranslator.getTravelComponents;
import static org.rsa.util.EmbedBuilderUtil.getActivitySummaryEmbedBuilder;
import static org.rsa.util.EmbedBuilderUtil.getTravelEmbedBuilder;

public class UserZoneManager {
    private static final Map<String, Integer> userZoneMap = new HashMap<>();

    public static void userTravelToZone(String userId, Integer zoneId) {
        userZoneMap.put(userId, zoneId);
    }

    public static int getUserCurrentZone(String userId) {
        return userZoneMap.getOrDefault(userId, Zone.START_TOWN.getId());
    }

    public static void travelToTown(ButtonInteractionEvent event, Guild guild, Member requester) {
        ActivityPerformResponse travelSummary = TravelSummaryManager.getUserSummary(requester.getId());
        UserAdventureProfile adventureProfile = UserAdventureProfileManager.fetch(guild.getId(), requester.getId());
        EmbedBuilder builder = getActivitySummaryEmbedBuilder(guild, requester, adventureProfile, "Travel Summary", travelSummary);

        event
            .editMessage(MessageEditData.fromEmbeds(builder.build()))
            .setComponents(ActionRow.of(
                Button.success("travel_select", "Travel"),
                Button.primary("view_profile", "Profile")
            ))
            .queue();

        TravelSummaryManager.clearTravelSummary(requester.getId());
    }

    public static void travelToZone(ButtonInteractionEvent event, Member requester, UserAdventureProfile adventureProfile, ZoneEntity zone) {
        UserZoneManager.userTravelToZone(requester.getId(), zone.getId());
        EmbedBuilder builder = getTravelEmbedBuilder(event.getGuild(), requester, zone);
        List<ItemComponent> components = getTravelComponents(adventureProfile, zone);
        event
            .editMessage(MessageEditData.fromEmbeds(builder.build()))
            .setComponents(ActionRow.of(components))
            .queue();
    }
}
