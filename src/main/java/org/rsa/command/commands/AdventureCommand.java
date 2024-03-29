package org.rsa.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;
import org.rsa.register.adventure.EntityManagerRegister;
import org.rsa.command.CommandObject;
import org.rsa.command.SubcommandObject;
import org.rsa.command.subcommands.adventure.AdventureIndexSubcommand;
import org.rsa.command.subcommands.adventure.AdventureProfileSubcommand;
import org.rsa.command.subcommands.adventure.AdventureSettingsSubcommand;
import org.rsa.command.subcommands.adventure.AdventureTravelSubcommand;
import org.rsa.entity.adventure.ZoneEntity;
import org.rsa.exception.ValidationException;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.util.Collections;
import java.util.List;

public class AdventureCommand extends CommandObject {

    private static final SubcommandObject[] subcommands = {
        new AdventureProfileSubcommand(),
        new AdventureTravelSubcommand(),
        new AdventureIndexSubcommand(),
        new AdventureSettingsSubcommand()
    };

    public AdventureCommand() {
        super("adventure", "Various commands for the adventure side of PandaBot", Collections.emptyList(), List.of(subcommands));
        setIsGuildOnly();
        setIsAutocomplete();
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        AutoCompleteQuery focusedOption = event.getFocusedOption();

        if (guild == null || member == null) {
            event.replyChoices(Collections.emptyList()).queue();
            return;
        }

        UserAdventureProfile adventureProfile = UserAdventureProfileManager.fetch(guild.getId(), member.getId());
        List<Integer> unlockedZoneIds = adventureProfile.getUnlockedZones();
        List<ZoneEntity> allZones = EntityManagerRegister.zoneManager.getEntityList();
        List<ZoneEntity> unlockedZones = allZones.stream().filter(zone -> unlockedZoneIds.contains(zone.getId())).toList();
        List<Command.Choice> options = unlockedZones.stream()
            .filter(zone -> zone.getName().toLowerCase().startsWith(focusedOption.getValue().toLowerCase()))
            .map(zone -> new Command.Choice(zone.getName(), zone.getId()))
            .limit(25)
            .toList();
        event.replyChoices(options).queue();
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) throws ValidationException {
        processSubcommand(event, subcommands, event.getSubcommandName());
    }
}
