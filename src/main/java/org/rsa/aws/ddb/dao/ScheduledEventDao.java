package org.rsa.aws.ddb.dao;

import org.rsa.aws.DynamoDB;
import org.rsa.aws.ddb.DeleteItemResponseWithStatus;
import org.rsa.aws.ddb.PutItemResponseWithStatus;
import org.rsa.aws.ddb.UpdateItemResponseWithStatus;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

public class ScheduledEventDao {

    private static final String TABLE_NAME = "scheduled_event_data";

    public static PutItemResponseWithStatus writeEventRole(String guildId, String eventId, String roleId) {
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

    public static PutItemResponseWithStatus writeEventChannel(String guildId, String channelId) {
        HashMap<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("guildid", AttributeValue.builder().s(guildId).build());
        itemValues.put("eventid", AttributeValue.builder().s("null").build());
        itemValues.put("channelid", AttributeValue.builder().s(channelId).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(itemValues)
                .build();

        return DynamoDB.handleRequest(request);
    }

    public static String read(String guildId, String eventId, String field) {
        Map<String, AttributeValue> attrValue = new HashMap<>();
        attrValue.put(":guildid", AttributeValue.builder().s(guildId).build());
        attrValue.put(":eventid", AttributeValue.builder().s(eventId).build());

        QueryRequest request = QueryRequest.builder()
                .tableName(TABLE_NAME)
                .keyConditionExpression("#guildid = :guildid and #eventid = :eventid")
                .expressionAttributeNames(getAttributeMapForEvent(guildId, eventId))
                .expressionAttributeValues(attrValue)
                .build();

        QueryResponse response = DynamoDB.query(request);
        List<Map<String, AttributeValue>> items = response.items();
        if (items.isEmpty()) {
            return null;
        }

        Map<String, AttributeValue> firstEntry = items.get(0);
        if (firstEntry.containsKey(field)) {
            return firstEntry.get(field).s();
        }
        return null;
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

    public static UpdateItemResponseWithStatus updateMessageListForEvent(
            String guildId,
            String eventId,
            String messageId) {
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put("guildid", AttributeValue.builder().s(guildId).build());
        keyToGet.put("eventid", AttributeValue.builder().s(eventId).build());

        List<String> messageIdL = getMessageListForEvent(guildId, eventId);
        if (messageIdL.get(0).isBlank()) {
            messageIdL.remove(0);
        }
        messageIdL.add(messageId);

        HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<>();
        updatedValues.put("messagelist", AttributeValueUpdate.builder()
                .value(AttributeValue.fromS(String.join(",", messageIdL)))
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(keyToGet)
                .attributeUpdates(updatedValues)
                .build();

        return DynamoDB.updateItem(request);
    }

    private static Map<String, String> getAttributeMapForEvent(String guildId, String eventId) {
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#guildid", "guildid");
        attrNames.put("#eventid", "eventid");
        return attrNames;
    }

    public static List<String> getMessageListForEvent(String guildId, String eventId) {
        String messageIdS = read(guildId, eventId, "messagelist");
        if (messageIdS == null) {
            messageIdS = "";
        }
        return new ArrayList<>(Arrays.asList(messageIdS.split(",")));
    }
}
