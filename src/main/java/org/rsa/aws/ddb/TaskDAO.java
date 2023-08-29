package org.rsa.aws.ddb;

import org.rsa.aws.DynamoDB;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;

public class TaskDAO {

    private static final String TABLE_NAME = "task_data";

    public static PutItemResponseWithStatus writeTask(String guildId, String taskName,
                                 String roleRewardId, String taskPrompt,
                                 String testFile) {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("guildid", AttributeValue.builder().s(guildId).build());
        itemValues.put("taskname", AttributeValue.builder().s(taskName).build());
        itemValues.put("roleid", AttributeValue.builder().s(roleRewardId).build());
        itemValues.put("taskprompt", AttributeValue.builder().s(taskPrompt).build());
        itemValues.put("testfile", AttributeValue.builder().s(testFile).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(itemValues)
                .build();

        return DynamoDB.handleRequest(request);
    }
}
