package org.rsa.command.commands;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.Bot;
import org.rsa.command.CommandObject;
import org.rsa.logic.constants.GuildConfigurationConstant;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.managers.ReputationManager;
import org.rsa.logic.data.models.GuildConfiguration;
import org.rsa.logic.data.models.UserReputation;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.rsa.util.ConversionUtil.parseIntFromString;

public class ResolveCommand extends CommandObject {

    public ResolveCommand() {
        super("resolve", "Mark a question as answered.");
        addOptionData(new OptionData(OptionType.USER, "helper-1", "Who answered your question?", true));
        addOptionData(new OptionData(OptionType.USER, "helper-2", "Who answered your question?", false));
        addOptionData(new OptionData(OptionType.USER, "helper-3", "Who answered your question?", false));
        addOptionData(new OptionData(OptionType.USER, "helper-4", "Who answered your question?", false));
        addOptionData(new OptionData(OptionType.USER, "helper-5", "Who answered your question?", false));
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
        Member requester = guild.retrieveMember(originalMessage.getAuthor()).complete();
        Member resolver = event.getMember();

        if (null == requester || null == resolver) {
            LoggerFactory.getLogger(Bot.class).warn("Request: {}.", requester);
            LoggerFactory.getLogger(Bot.class).warn("Resolver: {}.", resolver);
            event
                .reply("An error occurred. Please try again later.")
                .setEphemeral(true)
                .queue();
            return;
        }

        String resolverOverrideRoleId = guildConfig.getResolver_role_id();
        boolean canResolve = false;
        boolean wasOverridden = false;

        if (requester.getId().equals(resolver.getId())) {
            canResolve = true;
        } else if (!resolverOverrideRoleId.isEmpty() && !resolverOverrideRoleId.isBlank()) {
            Role resolveOverrideRole = guild.getRoleById(resolverOverrideRoleId);
            if (null != resolveOverrideRole) {
                if (resolver.getRoles().contains(resolveOverrideRole)) {
                    canResolve = true;
                    wasOverridden = true;
                }
            }
        }

        if (!canResolve) {
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

        List<Member> helperList = getHelperList(event);
        String response = "There was a problem resolving your question. Please try again later.";
        boolean canClose = false;
        int resolveReputation = parseIntFromString(guildConfig.getValue(GuildConfigurationConstant.RESOLVER_REPUTATION.getKey()));
        int helpingReputation = parseIntFromString(guildConfig.getValue(GuildConfigurationConstant.HELPER_REPUTATION.getKey()));
        System.out.println("Resolver reputation: " + resolveReputation);
        System.out.println("Helper reputation: " + helpingReputation);

        if (helperList.isEmpty()) {
            response = "You cannot resolve this question without giving credit.";
        } else if (helperList.size() == 1) {
            Member helper = helperList.get(0);
            if (helper.getId().equals(requester.getId())) {
                response = "Congrats on answering your own question! (Hopefully you shared your findings to help others in the future.)";
            } else {
                rewardHelper(guild, helper, requester, threadTitle, threadChannel.getJumpUrl(), helpingReputation);
                response = "Successfully resolved this question.";
            }
            canClose = true;
        } else {
            for (Member helper : helperList) {
                System.out.println(helper.getEffectiveName());
                Optional<ThreadMember> threadHelper = threadMemberList.stream().filter(threadMember -> threadMember.getId().equals(helper.getId())).findFirst();
                if (threadHelper.isEmpty()) continue;
                rewardHelper(guild, helper, requester, threadTitle, threadChannel.getJumpUrl(), helpingReputation);
                response = "Successfully resolved this question.";
                canClose = true;
            }

            if (!canClose) {
                response = "No valid helpers were listed. Please provide a valid helper to resolve this question.";
            }
        }

        if (wasOverridden) {
            response = "You have overridden the resolving for this question.";
            String helperListConcat = helperList.stream().map(Member::getUser).map(User::getId).map(id -> "<@" + id + ">").collect(Collectors.joining(","));
            messageHelper(requester, "Your question \"[" + threadTitle + "](<" + threadChannel.getJumpUrl() + ">)\" was overridden by " + resolver.getAsMention() + ".\nHelper(s): " + helperListConcat);
        }

        event.reply(response).setEphemeral(true).complete();
        if (canClose) {
            if (!wasOverridden) {
                awardReputation(guild, requester, resolveReputation);
            }
            threadChannel
                .getManager()
                .setName(String.join(" ", "[✅]", threadTitle))
                .setLocked(true)
                .setArchived(true)
                .queue();
        }
    }

    private List<Member> getHelperList(SlashCommandInteractionEvent event) {
        List<Member> helperList = new ArrayList<>();
        for (int i=0;i<5;i++) {
            Member helper = event.getOption("helper-" + (i+1), OptionMapping::getAsMember);
            helperList.add(helper);
        }

        return removeInvalidEntries(helperList);
    }

    private List<Member> removeInvalidEntries(List<Member> list) {
        return list.stream()
            .filter(Objects::nonNull)
            .filter(member -> !member.getUser().isBot())
            .filter(member -> !member.getUser().isSystem())
            .distinct()
            .collect(Collectors.toList());
    }

    private void rewardHelper(Guild guild, Member helper, Member requester, String threadTitle, String threadUrl, int reputation) {
        System.out.println("Rewarding " + helper.getEffectiveName());
        if (helper.getId().equals(requester.getId())) {
            System.out.println("Skipping " + helper.getEffectiveName());
            return;
        }
        String message = "Thank you for helping " + requester.getAsMention() + " with their question: [" + threadTitle + "](<" + threadUrl + ">).\nYou have been rewarded 0 reputation.";
        messageHelper(helper, message);
        awardReputation(guild, helper, reputation);
    }

    private void awardReputation(Guild guild, Member helper, int reputation) {
        UserReputation helperReputation = ReputationManager.fetch(guild.getId(), helper.getId());
        int currentReputation = helperReputation.getReputation();
        helperReputation.setReputation(currentReputation + reputation);

        int timesHelped = helperReputation.getTimes_helped();
        helperReputation.setTimes_helped(timesHelped + 1);

        ReputationManager.update(helperReputation);
    }

    private void messageHelper(Member helper, String message) {
        User user = helper.getUser();
        PrivateChannel channel = user.openPrivateChannel().complete();
        try {
            channel.sendMessage(message).complete();
        } catch (ErrorResponseException e) {
            LoggerFactory.getLogger(Bot.class).warn("Cannot DM {}, skipping.", helper.getId());
        }
    }
}
