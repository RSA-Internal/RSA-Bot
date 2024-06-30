package org.rsa.command.ai;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.panda.jda.command.CommandObjectV2;
import org.panda.jda.command.EventEntities;
import org.rsa.aws.bedrock.claude.Converse;
import org.rsa.aws.bedrock.claude.ConverseDetailedResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssistCommand extends CommandObjectV2 {

    private final List<String> allowedUsers = new ArrayList<>() {{
        add("169208961533345792");
    }};

    private final Map<String, String> personalityPrefix = new HashMap<>() {{
       put("playful", "Respond to this message in a playful manner");
       put("drunkensailor", "Respond to this message like a drunken sailor");
       put("professional", "Respond to this message as a professional");
       put("detailed", "Respond to the message with way to much detail");
    }};

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
    }

    @Override
    public void processSlashCommand(@NotNull EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();
        Member member = entities.getRequester();

        if (!allowedUsers.contains(member.getId())) {
            event.reply("You are not allow listed to use this command.").setEphemeral(true).queue();
            return;
        }

        String inputQuery = event.getOption("request", OptionMapping::getAsString);
        String context = event.getOption("context", "general", OptionMapping::getAsString);
        boolean reset = event.getOption("reset", false, OptionMapping::getAsBoolean);
        String personality = event.getOption("personality", OptionMapping::getAsString);
        if (null == inputQuery) {
            event.reply("There was not input provided.").setEphemeral(true).queue();
            return;
        }

        if (context.equals("roblox") && !inputQuery.toLowerCase().contains("roblox")) {
            inputQuery = inputQuery + " in roblox";
        }

        event.deferReply().queue();
        ConverseDetailedResponse modelResponse = Converse.converse(member.getId(), inputQuery, reset, personality);
        if (null == modelResponse) {
            event.getHook().editOriginal("An error occurred processing your input.").queue();
            return;
        }

        String content = modelResponse.getContent();
        if (content.length() > 2000) {
            String first = content.substring(0, 2000);
            int idx = 2000;
            Message lastMessage = event.getHook().editOriginal(first).complete();
            while (idx < content.length()) {
                int length = Math.min(content.length() - idx, 2000);
                String msg = content.substring(idx, idx + length);
                lastMessage = lastMessage.reply(msg).complete();
                idx += length;
            }
        } else {
            event.getHook().editOriginal(content).queue();
        }
    }
}
