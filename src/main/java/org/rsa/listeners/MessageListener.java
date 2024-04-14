package org.rsa.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;
import org.rsa.logic.constants.GuildConfigurationConstant;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        if (event.getAuthor().isBot() || event.getAuthor().isSystem()) return;

        Guild guild = event.getGuild();
        Channel channel = event.getChannel();

        GuildConfiguration guildConfig = GuildConfigurationManager.fetch(guild.getId());
        String helpChannelId = guildConfig.getHelp_channel_id();
        String helpRoleId = guildConfig.getHelper_role_id();
        String requiredChars = guildConfig.getReq_chars_for_help_thread();
        String titleLength = guildConfig.getHelp_thread_title_length();
        int requiredCharsValue = parseIntFromString(requiredChars, parseIntFromString(guildConfig.getValue(GuildConfigurationConstant.REQUIRED_CHARACTERS.getKey())));
        int titleLengthValue = parseIntFromString(titleLength, parseIntFromString(guildConfig.getValue(GuildConfigurationConstant.HELP_TITLE_LENGTH.getKey())));

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

        String pollChannel = guildConfig.getPoll_channel_id();
        boolean isInPollChannel = channel.getId().equals(pollChannel);

        if (!isInPollChannel) {
            DataObject rawData = event.getRawData();
            if (Objects.nonNull(rawData)) {
                if (rawData.toPrettyString().contains("poll")) {
                    System.out.println("Poll detected, deleting.");
                    event.getMessage().reply("Please do not send polls outside of the poll channel.").queue(m -> {
                        event.getMessage().delete().queue();
                        m.delete().queueAfter(5, TimeUnit.SECONDS);
                    });
                }
            }
        }
    }
}
