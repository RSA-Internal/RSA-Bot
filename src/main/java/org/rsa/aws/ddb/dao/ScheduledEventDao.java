package org.rsa.aws.ddb.dao;

import org.rsa.aws.DynamoDB;
import org.rsa.aws.ddb.DeleteItemResponseWithStatus;
import org.rsa.aws.ddb.PutItemResponseWithStatus;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

public class ScheduledEventDao {

    private static final String TABLE_NAME = "scheduled_event_data";

    public static PutItemResponseWithStatus write(String guildId, String eventId, String roleId) {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("guildid", AttributeValue.builder().s(guildId).build());
        itemValues.put("eventid", AttributeValue.builder().s(eventId).build());
        itemValues.put("roleid", AttributeValue.builder().s(roleId).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(itemValues)
                .build();

        return DynamoDB.handleRequest(request);
    }

    public static String read(String guildId, String eventId) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#guildid", "guildid");
        attrNames.put("#eventid", "eventid");
        Map<String, AttributeValue> attrValue = new HashMap<>();
        attrValue.put(":guildid", AttributeValue.builder().s(guildId).build());
        attrValue.put(":eventid", AttributeValue.builder().s(eventId).build());

        QueryRequest request = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("#guildid = :guildid and #eventid = :eventid")
                .expressionAttributeNames(attrNames)
                .expressionAttributeValues(attrValue)
                .build();

        QueryResponse response = DynamoDB.query(request);
        List<Map<String, AttributeValue>> items = response.items();
        if (items.isEmpty()) {
            return null;
        }

        Map<String, AttributeValue> firstEntry = items.get(0);
        return firstEntry.get("roleid").s();
    }

    public static DeleteItemResponseWithStatus delete(String guildId, String eventId) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("guildid", AttributeValue.builder().s(guildId).build());
        keyToGet.put("eventid", AttributeValue.builder().s(eventId).build());

        DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(keyToGet)
                .build();

        return DynamoDB.deleteItem(deleteItemRequest);
    }
}
