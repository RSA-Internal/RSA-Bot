package org.rsa.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.CommandObject;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;

import java.util.List;
import java.util.Optional;

import static org.rsa.util.ConversionUtil.parseIntFromString;

public class ResolveCommand extends CommandObject {

    public ResolveCommand() {
        super("resolve", "Mark a question as answered.");
        addOptionData(
            new OptionData(OptionType.USER, "helper", "Who answered your question?", true)
        );
    }

    @Override
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event
                .reply("This command can only be used in a Server.")
                .setEphemeral(true)
                .queue();
            return;
        }

        Channel channel = event.getChannel();

        if (channel.getType() != ChannelType.GUILD_PUBLIC_THREAD) {
            event
                .reply("You can only resolve questions that are in threads.")
                .setEphemeral(true)
                .queue();
            return;
        }

        ThreadChannel threadChannel = (ThreadChannel) channel;
        TextChannel parentChannel = threadChannel.getParentMessageChannel().asTextChannel();

        GuildConfiguration guildConfig = GuildConfigurationManager.fetch(guild.getId());
        String helpChannelId = guildConfig.getHelp_channel_id();
        TextChannel helpChannel = guild.getTextChannelById(helpChannelId);
        if (null == helpChannel) {
            event
                .reply("This guild does not have an active help channel to ask questions in, nothing to resolve.")
                .setEphemeral(true)
                .queue();
            return;
        }

        if (!helpChannelId.equals(parentChannel.getId())) {
            event
                .reply("You can only resolve questions that have been asked in the guild's provided help channel: " + helpChannel.getAsMention() + ".")
                .setEphemeral(true)
                .queue();
            return;
        }

        Message originalMessage = threadChannel.retrieveParentMessage().complete();
        Member requester = originalMessage.getMember();
        Member resolver = event.getMember();

        if (null == requester || null == resolver) {
            event
                .reply("An error occurred. Please try again later.")
                .setEphemeral(true)
                .queue();
            return;
        }

        if (!requester.getId().equals(resolver.getId())) {
            event
                .reply("You cannot resolve a question asked by someone else.")
                .setEphemeral(true)
                .queue();
            return;
        }

        List<ThreadMember> threadMemberList = threadChannel.retrieveThreadMembers().complete();
        int length = originalMessage.getContentStripped().length();

        String titleLength = guildConfig.getHelp_thread_title_length();
        int titleLengthValue = parseIntFromString(titleLength, 80);

        String threadTitle = originalMessage.getContentStripped().substring(0, Math.min(length, titleLengthValue));

        Member helper = event.getOption("helper", OptionMapping::getAsMember);
        String response = "There was a problem resolving your question. Please try again later.";
        boolean canClose;
        if (null != helper) {
            if (helper.getId().equals(requester.getId())) {
                response = "Congrats on answering your own question! (Hopefully you shared your findings to help others in the future.)";
                canClose = true;
            } else {
                Optional<ThreadMember> threadHelper = threadMemberList.stream().filter(threadMember -> threadMember.getId().equals(helper.getId())).findFirst();
                if (threadHelper.isEmpty()) {
                    canClose = false;
                    response = "Marked helper was not a part of this thread. Please provide a valid helper.";
                } else {
                    // TODO: Notify member of reputation gain.
                    helper.getUser().openPrivateChannel().queue(helperDm -> {
                        helperDm
                            .sendMessage("Thank you for helping " + requester.getAsMention() + " with their question: [" + threadTitle + "](" + threadChannel.getJumpUrl() + ").\nYou have been rewarded 0 reputation.")
                            .queue();
                    });

                    response = "Successfully resolved your question.";
                    canClose = true;
                }
            }
        } else {
            canClose = false;
        }

        event.reply(response).setEphemeral(true).queue(s -> {
            if (canClose) {
                threadChannel
                    .getManager()
                    .setName(String.join(" ", "[✅]", threadTitle))
                    .setLocked(true)
                    .setArchived(true)
                    .queue();
            }
        });
    }
}
