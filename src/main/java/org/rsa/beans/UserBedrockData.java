package org.rsa.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.rsa.aws.bedrock.ConverseDetailedResponse;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.HashMap;
import java.util.Map;

@DynamoDbBean
@Getter
@Setter
public class UserBedrockData {
    Integer schema_version = 1;
    String userId;

    Integer availableTokens;
    Map<String, ModelData> modelDataMap;

    public UserBedrockData() {}
    public UserBedrockData(String userId) {
        this.userId = userId;
        this.availableTokens = 0;
        this.modelDataMap = new HashMap<>();
    }

    public void init() {
        this.availableTokens = 1000;
        this.modelDataMap = new HashMap<>();
    }

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    public void processConverse(ConverseDetailedResponse modelResponse, Integer estimatedTokenUsage) {
        ModelData modelData;

        String modelId = modelResponse.getModelId();
        if (modelDataMap.containsKey(modelId)) {
            modelData = modelDataMap.get(modelId);
        } else {
            modelData = new ModelData();
        }

        modelData.processInvocation(modelResponse, estimatedTokenUsage);
        modelDataMap.put(modelId, modelData);
    }
}

