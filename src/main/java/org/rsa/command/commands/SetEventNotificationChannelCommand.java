package org.rsa.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.aws.ddb.PutItemResponseWithStatus;
import org.rsa.aws.ddb.dao.ScheduledEventDao;
import org.rsa.command.CommandObject;
import org.rsa.exception.ValidationException;

public class SetEventNotificationChannelCommand extends CommandObject {

    public SetEventNotificationChannelCommand() {
        super("set-event-channel", "Sets the event notification channel.");
        addOptionData(new OptionData(OptionType.CHANNEL, "channel",
                "The channel for event notifications.", true));
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) throws ValidationException {
        Channel channel = event.getOption("channel", OptionMapping::getAsChannel);
        if (channel == null || channel.getType() != ChannelType.TEXT) {
            event.reply("An invalid text channel was provided. Please try again.").setEphemeral(true).queue();
            return;
        }

        String channelId = channel.getId();
        Guild guild = event.getGuild();
        if (guild == null) {
            event
                    .reply("Could not fetch guild data. Your change has not been saved, please try again.")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        PutItemResponseWithStatus response = ScheduledEventDao.writeEventChannel(event.getGuild().getId(), channelId);
        event
                .reply(String.format("[Status: %s] - %s", response.failed() ? "Failed" : "Success", response.message()))
                .setEphemeral(true)
                .queue();
    }
}
