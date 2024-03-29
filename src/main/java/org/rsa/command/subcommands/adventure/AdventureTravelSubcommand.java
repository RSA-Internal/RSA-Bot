package org.rsa.command.subcommands.adventure;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.rsa.register.adventure.EntityManagerRegister;
import org.rsa.manager.adventure.TravelSummaryManager;
import org.rsa.manager.adventure.UserZoneManager;
import org.rsa.model.adventure.entity.Zone;
import org.rsa.command.SubcommandObject;
import org.rsa.entity.adventure.ZoneEntity;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.util.List;

import static org.rsa.translator.AdventureTravelTranslator.getTravelComponents;
import static org.rsa.util.EmbedBuilderUtil.getTravelEmbedBuilder;

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
        TravelSummaryManager.createNewTravelSummary(requester.getId());
        Integer zoneId = event.getOption("zone", 0, OptionMapping::getAsInt);
        UserZoneManager.userTravelToZone(requester.getId(), zoneId);
        ZoneEntity zone = EntityManagerRegister.zoneManager.getEntityById(zoneId);

        if (Zone.START_TOWN.getId().equals(zone.getId())) {
            event.reply("You've returned to " + zone.getName() + ".").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder builder = getTravelEmbedBuilder(guild, requester, zone);
        List<ItemComponent> components = getTravelComponents(adventureProfile, zone);
        event.replyEmbeds(builder.build()).addActionRow(components).queue();
    }
}
