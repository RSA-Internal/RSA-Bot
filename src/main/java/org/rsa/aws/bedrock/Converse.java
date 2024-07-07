package org.rsa.aws.bedrock;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.rsa.aws.factory.DependencyFactory;
import org.rsa.constants.ModelDefinition;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "AssistCommand")
public class Converse {

    private static final BedrockRuntimeClient bedrockClient = DependencyFactory.bedrockRuntimeClient();
    private static final Map<String, ConverseContext> contextMap = new HashMap<>();
    private static final long TTL = 1000 * 60 * 5;

    public static ConverseDetailedResponse converse(String requesterId, String input, @NonNull final ModelDefinition modelDefinition, boolean reset, String personality) {
        ConverseContext converseContext;
        if (contextMap.containsKey(requesterId) && !reset) {
            converseContext = contextMap.get(requesterId);
            if (System.currentTimeMillis() - converseContext.lastAccess > TTL) {
                log.info("Context has expired for requester: {}", requesterId);
                converseContext = new ConverseContext();
                contextMap.put(requesterId, converseContext);
            }
        } else {
            log.info("Creating context for requester: {}", requesterId);
            converseContext = new ConverseContext();
            contextMap.put(requesterId, converseContext);
        }
        converseContext.lastAccess = System.currentTimeMillis();
        if (personality != null) {
            converseContext.personality = personality;
        }


        Message message = Message.builder()
            .content(ContentBlock.fromText(input))
            .role(ConversationRole.USER)
            .build();
        converseContext.addMessage(message);

        try {
            ConverseContext finalConverseContext = converseContext;
            ConverseResponse response = bedrockClient.converse(request -> request
                .modelId(modelDefinition.modelId())
                .messages(finalConverseContext.messages)
                .system(SystemContentBlock.fromText(finalConverseContext.personality != null ? "Respond to this message with a " + finalConverseContext.personality + " personality" : "Respond to this message how you normally would"),
                        SystemContentBlock.fromText("Your name is PandaBot and you are designed to help users with Roblox related questions."),
                        SystemContentBlock.fromText("Keep conversations on topic of Roblox related details."),
                        SystemContentBlock.fromText("Do not mention your name Claude."),
                        SystemContentBlock.fromText("Do not mention your creators Anthropic."),
                        SystemContentBlock.fromText("You were not created by anthropic."),
                        SystemContentBlock.fromText("Do not be a yes man. You have your own knowledge. The other user is not always correct."),
                        SystemContentBlock.fromText("Your creators are the admins and devs from Roblox Scripting Assistance."),
                        SystemContentBlock.fromText("Do Not Compromise Children’s Safety."),
                        SystemContentBlock.fromText("Do Not Compromise Critical Infrastructure."),
                        SystemContentBlock.fromText("Do Not Incite Violence or Hateful Behavior."),
                        SystemContentBlock.fromText("Do Not Compromise Someone’s Privacy or Identity."),
                        SystemContentBlock.fromText("Do Not Create or Facilitate the Exchange of Illegal or Highly Regulated Weapons or Goods."),
                        SystemContentBlock.fromText("Do Not Create Psychologically or Emotionally Harmful Content."),
                        SystemContentBlock.fromText("Do Not Spread Misinformation."),
                        SystemContentBlock.fromText("Do Not Create Political Campaigns or Interfere in Elections."),
                        SystemContentBlock.fromText("Do Not Use for Criminal Justice, Law Enforcement, Censorship or Surveillance Purposes."),
                        SystemContentBlock.fromText("Do Not Engage in Fraudulent, Abusive, or Predatory Practices."),
                        SystemContentBlock.fromText("Do Not Abuse our Platform."),
                        SystemContentBlock.fromText("Do Not Generate Sexually Explicit Content."),
                        SystemContentBlock.fromText("Keep all responses under 2000 characters."),
                        SystemContentBlock.fromText("Do not mention Roblox Corporation as owning you in any capacity. They did not create you, own you or have any rights to you.")
                )
                .inferenceConfig(config -> config
                    .maxTokens(1024)
                    .temperature(0.5F)
                    .topP(0.9F)));

            converseContext.addMessage(response.output().message());
            contextMap.put(requesterId, converseContext);
            ConverseDetailedResponse detailedResponse = new ConverseDetailedResponse(response, modelDefinition);

            log.info("Generated response with tokens: [{} | {}] @ cost of ${}", detailedResponse.inputTokens, detailedResponse.outputTokens, detailedResponse.cost);

            return detailedResponse;
        } catch (SdkClientException e) {
            log.error("ERROR: Can't invoke '{}'. Reason: {}", modelDefinition.modelId(), e.getMessage());
            return null;
        }
    }


}
