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

import static org.rsa.logic.constants.GuildConfigurationConstants.HELP_CHANNEL_KEY;

public class ChannelsSubcommand extends SubcommandObject {

    public ChannelsSubcommand() {
        super("channels", "Configure server channels.");
        addOptions(
            new OptionData(OptionType.STRING, "channel_type", "Specify a channel type to configure.", true)
                .addChoice("help_channel", HELP_CHANNEL_KEY),
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

        String option = event.getOption("channel_type", OptionMapping::getAsString);
        if (option == null) {
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
        String value = channel.getId();

        String response = GuildConfigurationManager.processUpdate(guild, option, value);

        event
            .reply(response)
            .setEphemeral(true)
            .queue();
    }
}
