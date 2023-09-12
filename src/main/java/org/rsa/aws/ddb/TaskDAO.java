package org.rsa.aws.ddb;

import org.rsa.aws.DynamoDB;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.*;

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

    public static Set<String> getGuildTaskNameList(String guildId) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#guildid", "guildid");
        Map<String, AttributeValue> attrValue = new HashMap<>();
        attrValue.put(":guildid", AttributeValue.builder().s(guildId).build());

        QueryRequest request = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("#guildid = :guildid")
                .expressionAttributeNames(attrNames)
                .expressionAttributeValues(attrValue)
                .build();

        QueryResponse response = DynamoDB.query(request);
        Set<String> taskNameList = new HashSet<>();
        List<Map<String, AttributeValue>> items = response.items();
        items.forEach(stringAttributeValueMap ->
            stringAttributeValueMap.forEach((key, value) -> {
                if (key.equals("taskname")) {
                    taskNameList.add(value.s());
                }
            })
        );

        return taskNameList;
    }

    public static int getGuildTaskCount(String guildId) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#guildid", "guildid");
        Map<String, AttributeValue> attrValue = new HashMap<>();
        attrValue.put(":guildid", AttributeValue.builder().s(guildId).build());

        QueryRequest request = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("#guildid = :guildid")
                .expressionAttributeNames(attrNames)
                .expressionAttributeValues(attrValue)
                .build();

        QueryResponse response = DynamoDB.query(request);
        return response.count();
    }
}
