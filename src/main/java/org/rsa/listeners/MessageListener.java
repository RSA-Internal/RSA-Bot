package org.rsa.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.rsa.logic.constants.GuildConfigurationDefaults;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;

import static org.rsa.logic.constants.GuildConfigurationConstants.MESSAGE_CHARACTERS_AS_TITLE_KEY;
import static org.rsa.logic.constants.GuildConfigurationConstants.REQUIRED_CHARACTERS_KEY;
import static org.rsa.util.ConversionUtil.parseIntFromString;

public class MessageListener extends ListenerAdapter {

    private boolean isHelperActive(Guild guild, String helpChannelId, String helperRoleId) {
        return !helperRoleId.isEmpty() && !helperRoleId.isBlank() &&
            !helpChannelId.isEmpty() && !helpChannelId.isBlank() &&
            null != guild.getRoleById(helperRoleId) &&
            null != guild.getTextChannelById(helpChannelId);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);

        if (!event.isFromGuild()) return;

        Guild guild = event.getGuild();
        Channel channel = event.getChannel();

        GuildConfiguration guildConfig = GuildConfigurationManager.fetch(guild.getId());
        String helpChannelId = guildConfig.getHelp_channel_id();
        String helpRoleId = guildConfig.getHelper_role_id();
        String requiredChars = guildConfig.getReq_chars_for_help_thread();
        String titleLength = guildConfig.getHelp_thread_title_length();
        int requiredCharsValue = parseIntFromString(requiredChars, parseIntFromString(GuildConfigurationDefaults.DEFAULTS.get(REQUIRED_CHARACTERS_KEY), 50));
        int titleLengthValue = parseIntFromString(titleLength, parseIntFromString(GuildConfigurationDefaults.DEFAULTS.get(MESSAGE_CHARACTERS_AS_TITLE_KEY), 80));

        boolean isHelpActive = isHelperActive(guild, helpChannelId, helpRoleId);
        boolean isInHelpChannel = channel.getId().equals(helpChannelId);

        if (channel.getType() == ChannelType.TEXT && isHelpActive && isInHelpChannel) {

            Role helperRole = guild.getRoleById(helpRoleId);
            if (null == helperRole) return;

            Message message = event.getMessage();
            int length = message.getContentStripped().length();

            if (length >= requiredCharsValue) {
                message.createThreadChannel(
                    String.join(" ", "[❔]", message.getContentStripped().substring(0, Math.min(length, titleLengthValue)))
                ).queue(c -> c.sendMessage("A new question has been asked. " + helperRole.getAsMention()).queue());
            }
        }
    }
}
