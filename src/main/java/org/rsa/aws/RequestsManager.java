package org.rsa.aws;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.LinkedList;
import java.util.Queue;


public class RequestsManager<T> {
    private final Queue<UpdateItemEnhancedRequest<T>> _updateRequestsQueue = new LinkedList<>();
    private boolean _processingUpdateRequests = false;
    private final DynamoDbTable<T> _table;

    public RequestsManager(DynamoDbTable<T> table)
    {
        this._table = table;
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

    public void enqueue(UpdateItemEnhancedRequest<T> request)
    {
        _updateRequestsQueue.add(request);
        if (_processingUpdateRequests) return;
        processUpdateRequests();
    }
}
