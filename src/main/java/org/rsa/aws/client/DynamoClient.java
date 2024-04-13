package org.rsa.aws.client;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Objects;

public class DynamoClient {
    private static DynamoDbClient dynamoDbClient;
    private static DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private static DynamoClient instance;

    public static DynamoClient getInstance() {
        if (Objects.isNull(instance)) {
            instance = new DynamoClient();
        }
        return instance;
    }

    private DynamoClient() {
        dynamoDbClient = DynamoDbClient.builder().region(Region.US_WEST_2).build();
        dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }

    public DynamoDbClient getDynamoDbClient() {
        return dynamoDbClient;
    }

    public DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
        return dynamoDbEnhancedClient;
    }
}
