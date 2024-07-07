package org.rsa.managers;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import org.rsa.aws.RequestsManager;
import org.rsa.aws.bedrock.ConverseDetailedResponse;
import org.rsa.beans.UserBedrockData;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;

@Slf4j(topic = "AssistCommand")
public class BedrockDataManager {
    private final static String TABLE_NAME = "bedrock_data";
    private final static RequestsManager<UserBedrockData> requestManager = new RequestsManager<>(TABLE_NAME, UserBedrockData.class);

    public static UserBedrockData fetch(String userId) {
        log.info("Retrieving data for {}", userId);
        QueryConditional query = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(userId)
                        .build());
        Optional<Page<UserBedrockData>> optionalPage = requestManager.fetchSingleItem(query);

        if (optionalPage.isEmpty() || optionalPage.get().items().isEmpty()) {
            UserBedrockData bedrockData = new UserBedrockData(userId);
            bedrockData.init();
            update(bedrockData);
            return bedrockData;
        }
        return optionalPage.get().items().get(0);
    }

    public static void update(UserBedrockData item) {
        requestManager.enqueueItemUpdate(item);
    }

    public static void processConverse(UserBedrockData userBedrockData,
                                       ConverseDetailedResponse modelResponse,
                                       Integer estimatedTokenUsage) {
        userBedrockData.setAvailableTokens(userBedrockData.getAvailableTokens() - estimatedTokenUsage);
        userBedrockData.processConverse(modelResponse, estimatedTokenUsage);
        update(userBedrockData);
    }
}
