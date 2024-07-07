package org.rsa.constants;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


@Slf4j(topic = "AssistCommand")
public class BedrockModelConstants {

    public static final ModelDefinition ClaudeSonnet;
    public static final ModelDefinition Llama370b;
    public static final Map<String, ModelDefinition> values = new HashMap<>();
    private static final Map<String, String> modelIdLookup = new HashMap<>();

    static {
        ClaudeSonnet = new ModelDefinition("anthropic.claude-3-sonnet-20240229-v1:0", 0.003, 0.015);
        Llama370b = new ModelDefinition("meta.llama3-70b-instruct-v1:0", 0.00265, 0.0035);

        values.put("ClaudeSonnet", ClaudeSonnet);
        values.put("Llama3_70b", Llama370b);

        modelIdLookup.put("claudesonnet", "ClaudeSonnet");
        modelIdLookup.put("llama3_70b", "Llama3_70b");
    }

    public static ModelDefinition defaultModel() {
        return Llama370b;
    }

    public static ModelDefinition getModel(String modelIdKey) {
        if (modelIdKey == null) {
            log.info("No idKey provided, returning default model.");
            return defaultModel();
        }

        String modelId = modelIdLookup.get(modelIdKey);
        if (modelId == null) {
            log.info("Invalid idKey: {}, returning default model.", modelIdKey);
            return defaultModel();
        }

        log.info("Found modelId: {} for idKey: {}", modelId, modelIdKey);
        return values.get(modelId);
    }
}

