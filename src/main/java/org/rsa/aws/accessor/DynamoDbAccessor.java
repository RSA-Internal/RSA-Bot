package org.rsa.aws.accessor;

import org.rsa.Bot;
import org.rsa.aws.client.DynamoClient;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

public class DynamoDbAccessor {

    private static final DynamoClient dynamoClient = DynamoClient.getInstance();

    private static String validateTableName(String tableName) {
        System.out.println("Validating table name: " + tableName);
        System.out.println("Devo: " + Bot.isDev);
        System.out.println("DevTable: " + tableName.contains("dev"));
        if (Bot.isDev && !tableName.contains("dev")) {
            String newTableName = "dev_" + tableName;
            System.out.println("Currently in dev environment, provided table name does not reference dev table.");
            System.out.println("Old table name: " + tableName + ", New table name: " + newTableName);
            return newTableName;
        }
        return tableName;
    }

    public static <T> void createTable(String tableName, TableSchema<T> schema) {
        tableName = validateTableName(tableName);
        System.out.println("Creating table: " + tableName);
        DynamoDbTable<T> tableInstance = dynamoClient.getDynamoDbEnhancedClient().table(tableName, schema);
        try {
            tableInstance.createTable(builder -> builder
                .provisionedThroughput(b -> b
                    .readCapacityUnits(1L)
                    .writeCapacityUnits(1L)
                    .build()
                ));
        } catch (ResourceInUseException r) {
            // table already exists.
            System.out.println("Table already exists: " + tableName);
        }
    }

    public static <T> DynamoDbTable<T> getTable(String tableName, TableSchema<T> schema) {
        tableName = validateTableName(tableName);
        System.out.println("Retrieving table: " + tableName);
        DynamoDbTable<T> tableInstance = dynamoClient.getDynamoDbEnhancedClient().table(tableName, schema);
        try {
            tableInstance.describeTable();
            return tableInstance;
        } catch (ResourceNotFoundException r) {
            // table does not exist.
            createTable(tableName, schema);
            try (DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoClient.getDynamoDbClient()).build()) {
                ResponseOrException<DescribeTableResponse> response = waiter
                    .waitUntilTableExists(builder -> builder.tableName("Customer").build())
                    .matched();
                DescribeTableResponse tableDescription = response.response().orElseThrow(
                    () -> new RuntimeException("unable to create table."));
                // The actual error can be inspected in response.exception()
                //logger.info("Customer table was created.");
                System.out.println("GetTable created new table: " + tableName);
                return dynamoClient.getDynamoDbEnhancedClient().table(tableName, schema);
            } catch (Exception e) {
                System.out.println("GetTable could not find or create a table: " + tableName);
            }
        }

        return null;
    }
}
