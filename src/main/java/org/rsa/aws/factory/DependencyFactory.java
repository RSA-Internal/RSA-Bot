package org.rsa.aws.factory;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import software.amazon.awssdk.services.s3.S3Client;

public class DependencyFactory {

    private DependencyFactory() {}

    /**
     * @return an instance of S3Client
     */
    public static S3Client s3Client() {
        return S3Client.builder().region(Region.US_WEST_2).build();
    }

    /**
     * @return an instance of DynamoDbClient
     */
    public static DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder().region(Region.US_WEST_2).build();
    }

    /**
     * @return an instance of DynamoDbEnhancedClient
     */
    public static DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient()).build();
    }

    /**
     * @return an instance of DynamoDbWaiter
     */
    public static DynamoDbWaiter dynamoDbWaiter() {
        return DynamoDbWaiter.builder().client(dynamoDbClient()).build();
    }
}
