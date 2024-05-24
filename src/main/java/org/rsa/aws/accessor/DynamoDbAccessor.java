package org.rsa.aws.accessor;

import lombok.extern.slf4j.Slf4j;
import org.rsa.Bot;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import static org.rsa.aws.factory.DependencyFactory.dynamoDbEnhancedClient;
import static org.rsa.aws.factory.DependencyFactory.dynamoDbWaiter;

@Slf4j
public class DynamoDbAccessor {

    private static String validateTableName(String tableName) {
        log.info("Validating table name: " + tableName);
        log.info("Devo: " + Bot.isDev);
        log.info("DevTable: " + tableName.contains("dev"));
        if (Bot.isDev && !tableName.contains("dev")) {
            String newTableName = "dev_" + tableName;
            log.info("Currently in dev environment, provided table name does not reference dev table.");
            log.info("Old table name: " + tableName + ", New table name: " + newTableName);
            return newTableName;
        }
        return tableName;
    }

    public static <T> void createTable(String tableName, TableSchema<T> schema) {
        tableName = validateTableName(tableName);
        log.info("Creating table: " + tableName);
        DynamoDbTable<T> tableInstance = dynamoDbEnhancedClient().table(tableName, schema);
        try {
            tableInstance.createTable(builder -> builder
                .provisionedThroughput(b -> b
                    .readCapacityUnits(1L)
                    .writeCapacityUnits(1L)
                    .build()
                ));
        } catch (ResourceInUseException r) {
            // table already exists.
            log.info("Table already exists: " + tableName);
        }
    }

    public static <T> DynamoDbTable<T> getTable(String tableName, TableSchema<T> schema) {
        tableName = validateTableName(tableName);
        log.info("Retrieving table: " + tableName);
        DynamoDbTable<T> tableInstance = dynamoDbEnhancedClient().table(tableName, schema);
        try {
            tableInstance.describeTable();
            return tableInstance;
        } catch (ResourceNotFoundException r) {
            // table does not exist.
            createTable(tableName, schema);
            try (DynamoDbWaiter waiter = dynamoDbWaiter()) {
                ResponseOrException<DescribeTableResponse> response = waiter
                    .waitUntilTableExists(builder -> builder.tableName("Customer").build())
                    .matched();
                DescribeTableResponse tableDescription = response.response().orElseThrow(
                    () -> new RuntimeException("unable to create table."));
                // The actual error can be inspected in response.exception()
                //logger.info("Customer table was created.");
                log.info("GetTable created new table: " + tableName);
                return dynamoDbEnhancedClient().table(tableName, schema);
            } catch (Exception e) {
                log.info("GetTable could not find or create a table: " + tableName);
            }
        }

        return null;
    }
}
