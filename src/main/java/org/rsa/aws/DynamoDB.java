package org.rsa.aws;

import lombok.extern.slf4j.Slf4j;
import org.rsa.aws.ddb.DeleteItemResponseWithStatus;
import org.rsa.aws.ddb.PutItemResponseWithStatus;
import org.rsa.aws.ddb.UpdateItemResponseWithStatus;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Slf4j
public class DynamoDB {

    private static DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.US_WEST_2)
                .build();
    }

    public static <T> DynamoDbTable<T> GetDynamoTable(String tableName, TableSchema<T> schema)
    {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getDynamoDbClient()).build();
        return enhancedClient.table(tableName, schema);
    }

    public static PutItemResponseWithStatus handleRequest(PutItemRequest putItemRequest) {
        DynamoDbClient dynamoDbClient = getDynamoDbClient();
        String tableName = putItemRequest.tableName();

        try {
            PutItemResponse response = dynamoDbClient.putItem(putItemRequest);
            log.info(tableName +" was successfully updated. The request id is "+response.responseMetadata().requestId());
            return new PutItemResponseWithStatus(response, false, "");
        } catch (ResourceNotFoundException e) {
            String errorMessage = String.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            log.error(errorMessage);
            return new PutItemResponseWithStatus(null, true, errorMessage);
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
            return new PutItemResponseWithStatus(null, true, e.getMessage());
        }
    }

    public static DeleteItemResponseWithStatus deleteItem(DeleteItemRequest deleteRequest) {
        DynamoDbClient dynamoDbClient = getDynamoDbClient();
        String tableName = deleteRequest.tableName();

        try {
            DeleteItemResponse response = dynamoDbClient.deleteItem(deleteRequest);
            log.info(tableName +" was successfully updated. The request id is "+response.responseMetadata().requestId());
            return new DeleteItemResponseWithStatus(response, false, "");
        } catch (ResourceNotFoundException e) {
            String errorMessage = String.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            log.error(errorMessage);
            return new DeleteItemResponseWithStatus(null, true, errorMessage);
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
            return new DeleteItemResponseWithStatus(null, true, e.getMessage());
        }
    }

    public static QueryResponse query(QueryRequest queryRequest) {
        DynamoDbClient dynamoDbClient = getDynamoDbClient();
        return dynamoDbClient.query(queryRequest);
    }

    public static UpdateItemResponseWithStatus updateItem(UpdateItemRequest updateRequest) {
        DynamoDbClient dynamoDbClient = getDynamoDbClient();
        String tableName = updateRequest.tableName();

        try {
            UpdateItemResponse response = dynamoDbClient.updateItem(updateRequest);
            log.info(tableName +" was successfully updated. The request id is "+response.responseMetadata().requestId());
            return new UpdateItemResponseWithStatus(response, false, "");
        } catch (ResourceNotFoundException e) {
            String errorMessage = String.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            log.error(errorMessage);
            return new UpdateItemResponseWithStatus(null, true, errorMessage);
        } catch (DynamoDbException e) {
            log.error(e.getMessage());
            return new UpdateItemResponseWithStatus(null, true, e.getMessage());
        }
    }
}
