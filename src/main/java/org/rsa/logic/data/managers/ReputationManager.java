package org.rsa.logic.data.managers;

import org.rsa.aws.DynamoDB;
import org.rsa.aws.RequestsManager;
import org.rsa.logic.data.models.UserReputation;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.Optional;

public final class ReputationManager {
    private final static String TABLE_NAME = "reputation_data";
    private final static DynamoDbTable<UserReputation> _table = DynamoDB.GetDynamoTable(TABLE_NAME, TableSchema.fromBean(UserReputation.class));
    private final static RequestsManager<UserReputation> _requestsManager = new RequestsManager<>(_table);

    public static UserReputation fetch(String guildId, String userId)
    {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(guildId)
                        .sortValue(userId)
                        .build());

        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(1)
                .scanIndexForward(false)
                .build();

        PageIterable<UserReputation> itemsPage = _table.query(request);
        Optional<Page<UserReputation>> optionalUserReputationPage = itemsPage.stream().findFirst();

        if (optionalUserReputationPage.isEmpty() || optionalUserReputationPage.get().items().isEmpty()) return new UserReputation(guildId, userId);
        return optionalUserReputationPage.get().items().get(0);
    }

    public static void update(UserReputation userReputation)
    {
        UpdateItemEnhancedRequest<UserReputation> updateRequest = UpdateItemEnhancedRequest.builder(UserReputation.class)
            .item(userReputation)
            .build();
        _requestsManager.enqueue(updateRequest);
    }
}
