package org.rsa.logic.data.models;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DynamoDbBean
@Data
public class DevforumUpdates {
    private Integer schema_version = 1;
    private String guildid;
    private String channelid;
    private List<Integer> enabled_topics;
    private Map<Integer, Integer> last_posts;

    public DevforumUpdates() {
        this.enabled_topics = new ArrayList<>();
        this.last_posts = new HashMap<>();
    }

    public DevforumUpdates(String guildId) {
        this();
        this.guildid = guildId;
    }

    @DynamoDbPartitionKey
    public String getGuildid() {
        return guildid;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "ChannelIdIndex")
    public String getChannelIdIndex() {
        return channelid;
    }
}
