package org.rsa.command.ai;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.SplitUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.panda.jda.command.CommandObjectV2;
import org.panda.jda.command.EventEntities;
import org.rsa.aws.bedrock.Converse;
import org.rsa.aws.bedrock.ConverseDetailedResponse;
import org.rsa.beans.UserBedrockData;
import org.rsa.constants.BedrockModelConstants;
import org.rsa.constants.ModelDefinition;
import org.rsa.managers.BedrockDataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssistCommand extends CommandObjectV2 {

    private final Integer charsPerToken = 6;
    private final List<String> allowedUsers = new ArrayList<>() {{
        add("169208961533345792");
    }};

    private final Map<String, String> personalityPrefix = new HashMap<>() {{
       put("playful", "Respond to this message in a playful manner");
       put("drunkensailor", "Respond to this message like a drunken sailor");
       put("professional", "Respond to this message as a professional");
       put("detailed", "Respond to the message with way to much detail");
    }};

    private final Map<String, Boolean> ctxLock = new HashMap<>();

    public AssistCommand() {
        super("assist", "Request AI assistance.");
        addOptionData(new OptionData(OptionType.STRING, "request", "Your input query", true));
        addOptionData(new OptionData(OptionType.STRING, "context", "Request context. [default: general]", false)
            .addChoice("Roblox", "roblox")
            .addChoice("General", "general"));
        addOptionData(new OptionData(OptionType.BOOLEAN, "reset", "Reset the context (the conversation)", false));
        addOptionData(new OptionData(OptionType.STRING, "personality", "The personality of the response", false)
            .addChoice("playful", "playful")
            .addChoice("drunken sailor", "drunkensailor")
            .addChoice("professional", "professional")
            .addChoice("detailed", "detailed")
            .addChoice("sexual", "seductive")
            .addChoice("uwu", "uwu-speak"));
        addOptionData(new OptionData(OptionType.STRING, "model", "The model to use", false)
            .addChoices(
                BedrockModelConstants
                    .values
                    .keySet()
                    .stream()
                    .map(name -> new Command.Choice(name, name.toLowerCase()))
                    .toList()));
    }

    @Override
    public void processSlashCommand(@NotNull EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();
        Member member = entities.getRequester();

        if (ctxLock.containsKey(member.getId()) && ctxLock.get(member.getId())) {
            event.reply("You currently have an assist command processing. Please wait.").setEphemeral(true).queue();
            return;
        }
        ctxLock.put(member.getId(), true);

        UserBedrockData userBedrockData = BedrockDataManager.fetch(member.getId());

        if (!allowedUsers.contains(member.getId())) {
            event.reply("You are not allow listed to use this command.").setEphemeral(true).queue();
            return;
        }

        String inputQuery = event.getOption("request", OptionMapping::getAsString);
        String context = event.getOption("context", "general", OptionMapping::getAsString);
        boolean reset = event.getOption("reset", false, OptionMapping::getAsBoolean);
        String personality = event.getOption("personality", OptionMapping::getAsString);
        String modelIdKey = event.getOption("model", OptionMapping::getAsString);
        if (null == inputQuery) {
            event.reply("There was not input provided.").setEphemeral(true).queue();
            return;
        }

        ModelDefinition modelDefinition = BedrockModelConstants.getModel(modelIdKey);
        if (context.equals("roblox") && !inputQuery.toLowerCase().contains("roblox")) {
            inputQuery = inputQuery + " in roblox";
        }

        int estimatedTokenUsage = inputQuery.length() / charsPerToken;

        if (userBedrockData.getAvailableTokens() <= 0) {
            event.reply("You do not have any input tokens available. Current token count: " + userBedrockData.getAvailableTokens()).setEphemeral(true).queue();
            return;
        }
        if (userBedrockData.getAvailableTokens() < estimatedTokenUsage) {
            event.reply(String.format(
                    "You do not have enough tokens to process this request. %nAvailable Tokens: %s | Required Tokens: %s",
                    userBedrockData.getAvailableTokens(),
                    estimatedTokenUsage))
                .setEphemeral(true)
                .queue();
            return;
        }

        event.deferReply(true).queue();
        InteractionHook hook = event.getHook();
        ConverseDetailedResponse modelResponse = Converse.converse(member.getId(), inputQuery, modelDefinition, reset, personality);
        if (null == modelResponse) {
            hook.editOriginal("An error occurred processing your input.").queue();
            return;
        }

        BedrockDataManager.processConverse(userBedrockData, modelResponse, estimatedTokenUsage);
        String content = modelResponse.getContent();
        if (content.length() > 2000) {
            List<String> parts = SplitUtil.split(content, 2000, SplitUtil.Strategy.WHITESPACE);
            String part0 = parts.get(0);
            hook.editOriginal(part0).queue();
            for (int i = 1; i < parts.size(); i++) {
                hook.setEphemeral(true).sendMessage(parts.get(i)).queue();
            }
        } else {
            hook.editOriginal(content).queue();
        }
        ctxLock.put(member.getId(), false);
    }
}
