package org.rsa.aws;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;


public class RequestsManager<T> {
    private final Queue<UpdateItemEnhancedRequest<T>> _updateRequestsQueue = new LinkedList<>();
    private final DynamoDbTable<T> _table;
    private final Class<T> _classType;
    private boolean _processingUpdateRequests = false;

    public RequestsManager(String tableName, Class<T> type)
    {
        this._table = DynamoDB.GetDynamoTable(tableName, TableSchema.fromBean(type));
        this._classType = type;
    }

    private void processUpdateRequests()
    {
        if (_updateRequestsQueue.isEmpty()) return;

        _processingUpdateRequests = true;
        UpdateItemEnhancedRequest<T> request = _updateRequestsQueue.remove();
        _table.updateItem(request);

        _processingUpdateRequests = false;
        processUpdateRequests();
    }

    public void enqueueItemUpdate(T item)
    {
        UpdateItemEnhancedRequest<T> request = UpdateItemEnhancedRequest.builder(_classType)
                .item(item)
                .build();
        enqueueUpdateRequest(request);
    }

    public void enqueueUpdateRequest(UpdateItemEnhancedRequest<T> request)
    {
        _updateRequestsQueue.add(request);
        if (_processingUpdateRequests) return;
        processUpdateRequests();
    }

    public Optional<Page<T>> fetchSingleItem(QueryConditional queryConditional)
    {
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
            .queryConditional(queryConditional)
            .limit(1)
            .scanIndexForward(false)
            .build();
        PageIterable<T> pages = _table.query(request);
        return pages.stream().findFirst();
    }
}
