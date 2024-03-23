package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;

public class ChannelsSubcommand extends SubcommandObject {

    public ChannelsSubcommand() {
        super("channels", "Configure server channels.");
        addOptions(
            new OptionData(OptionType.STRING, "channel_type", "Specify a channel type to configure.", true)
                .addChoice("help_channel", "help_channel"),
            new OptionData(OptionType.CHANNEL, "channel", "Specify the channel", true)
        );
    }

    @Override
    public void handleSubcommand(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event
                .reply("This command can only be used in a Server.")
                .setEphemeral(true)
                .queue();
            return;
        }

        String channelType = event.getOption("channel_type", OptionMapping::getAsString);
        if (channelType == null) {
            event
                .reply("An invalid channel type was provided. Please try again later.")
                .setEphemeral(true)
                .queue();
            return;
        }

        Channel channel = event.getOption("channel", OptionMapping::getAsChannel);
        if (channel == null) {
            event
                .reply("An invalid channel was provided. Please try again later.")
                .setEphemeral(true)
                .queue();
            return;
        }

        GuildConfiguration guildConfig = GuildConfigurationManager.fetch(guild.getId());

        if (channelType.equals("help_channel")) {
            guildConfig.setHelp_channel_id(channel.getId());
        }

        GuildConfigurationManager.update(guildConfig);
        event
            .reply("✅ **" + channelType + "** channel changed to " + channel.getAsMention())
            .setEphemeral(true)
            .queue();
    }
}
