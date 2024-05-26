package org.rsa.command.configure.subcommand.devforum;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.panda.jda.command.EventEntities;
import org.panda.jda.command.SubcommandObjectV2;
import org.rsa.logic.data.managers.DevforumUpdatesManager;

public class ConfigureDevforumChannel extends SubcommandObjectV2 {

    public ConfigureDevforumChannel() {
        super("channel", "Configure update channel.");

        addOptions(
            new OptionData(OptionType.CHANNEL, "channel", "Specify the update channel", true));
    }

    @Override
    public void processSlashCommandInteraction(EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();

        String channelId = event.getOption("channel", OptionMapping::getAsString);
        DevforumUpdatesManager devforumUpdatesManager = new DevforumUpdatesManager();
        String guildId = event.getGuild().getId();
        devforumUpdatesManager.changeChannelId(guildId, channelId);

        event.reply(String.format("Update channel set to %s", channelId)).setEphemeral(true).queue();
    }
}
