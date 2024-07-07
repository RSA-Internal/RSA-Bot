package org.rsa.aws.bedrock;

import lombok.Getter;
import org.rsa.constants.ModelDefinition;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;

@Getter
public class ConverseDetailedResponse {
    private static final double BILLING_UNIT = 1000;

    String content;
    String modelId;
    int inputTokens;
    int outputTokens;
    double cost;

    public ConverseDetailedResponse(ConverseResponse response, ModelDefinition modelDefinition) {
        content = response.output().message().content().get(0).text();
        inputTokens = response.usage().inputTokens();
        outputTokens = response.usage().outputTokens();
        modelId = modelDefinition.modelId();
        cost = getCostForTokenType(inputTokens, modelDefinition.inputTokenCost()) +
                getCostForTokenType(outputTokens, modelDefinition.outputTokenCost());
    }

    private double getCostForTokenType(int tokenCount, double tokenCost) {
        return (tokenCount / BILLING_UNIT) * tokenCost;
    }
}
