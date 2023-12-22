package org.rsa.aws.ddb;

import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;

public record DeleteItemResponseWithStatus(DeleteItemResponse deleteItemResponse, boolean failed, String message) {}