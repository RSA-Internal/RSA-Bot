package org.rsa.aws.bedrock.claude;

import lombok.Getter;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;

@Getter
public class ConverseDetailedResponse {
    private static final double INPUT_COST = 0.003;
    private static final double OUTPUT_COST = 0.015;
    private static final double BILLING_UNIT = 1000;

    String content;
    int inputTokens;
    int outputTokens;
    double cost;

    public ConverseDetailedResponse(ConverseResponse response) {
        content = response.output().message().content().get(0).text();
        inputTokens = response.usage().inputTokens();
        outputTokens = response.usage().outputTokens();
        cost = getCostForTokenType(inputTokens, INPUT_COST) + getCostForTokenType(outputTokens, OUTPUT_COST);
    }

    private double getCostForTokenType(int tokenCount, double tokenCost) {
        return (tokenCount / BILLING_UNIT) * tokenCost;
    }
}
