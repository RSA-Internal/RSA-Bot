package org.rsa.aws.ddb;

import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

public record PutItemResponseWithStatus(PutItemResponse putItemResponse, boolean failed, String message) {}
