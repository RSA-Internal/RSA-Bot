package org.rsa.aws.ddb;

import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

public record UpdateItemResponseWithStatus(UpdateItemResponse updateItemResponse, boolean failed, String message) {}
