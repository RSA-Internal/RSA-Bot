package org.rsa.aws;

import org.rsa.aws.ddb.PutItemResponseWithStatus;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

public class DynamoDB {

    private static DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();
    }

    public static PutItemResponseWithStatus handleRequest(PutItemRequest putItemRequest) {
        DynamoDbClient dynamoDbClient = getDynamoDbClient();
        String tableName = putItemRequest.tableName();

        try {
            PutItemResponse response = dynamoDbClient.putItem(putItemRequest);
            System.out.println(tableName +" was successfully updated. The request id is "+response.responseMetadata().requestId());
            return new PutItemResponseWithStatus(response, false, "");
        } catch (ResourceNotFoundException e) {
            String errorMessage = String.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.err.format(errorMessage);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            return new PutItemResponseWithStatus(null, true, errorMessage);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            return new PutItemResponseWithStatus(null, true, e.getMessage());
        }
    }
}
