package org.rsa.beans;

import lombok.Getter;
import lombok.Setter;
import org.rsa.aws.bedrock.ConverseDetailedResponse;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
@Getter
@Setter
public class ModelData {
    Integer inputTokens;
    Integer outputTokens;
    Integer invocations;

    public ModelData() {
        this.inputTokens = 0;
        this.outputTokens = 0;
        this.invocations = 0;
    }

    public void processInvocation(ConverseDetailedResponse modelResponse, Integer estimatedTokenUsage) {
        this.inputTokens += estimatedTokenUsage;
        this.outputTokens += modelResponse.getOutputTokens();
        this.invocations += 1;
    }
}
