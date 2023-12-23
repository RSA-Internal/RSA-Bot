package org.rsa.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.CommandObject;
import org.rsa.exception.ValidationException;
import org.rsa.listeners.ScheduledEventListener;

public class SetupEventCommand extends CommandObject {


    public SetupEventCommand() {
        super("setup-event", "Setup event management with the bot.");
        addOptionData(new OptionData(OptionType.STRING, "event-id", "The event id of the event to setup.", true));
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) throws ValidationException {
        String eventId = event.getOption("event-id", OptionMapping::getAsString);
        if (eventId == null) {
            event.reply("No event id was provided.").setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply("Something went wrong, please try again.").setEphemeral(true).queue();
            return;
        }

        ScheduledEvent scheduledEvent = event.getGuild().getScheduledEventById(eventId);
        if (scheduledEvent == null) {
            event.reply("There is no event with the provided id.").setEphemeral(true).queue();
            return;
        }

        if (ScheduledEventListener.setupEvent(guild, scheduledEvent)) {
            event.reply("Event setup successfully.").setEphemeral(true).queue();
        } else {
            event.reply("Event already setup, no actions taken.").setEphemeral(true).queue();
        }
    }
}
