package org.rsa.logic.data.models;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@Getter
@Setter
public class GuildConfiguration {
    Integer schema_version = 1;
    String guildid; // Partition key

    String upvote_emoji = "";
    String downvote_emoji = "";

    public GuildConfiguration() { }
    public GuildConfiguration(String guildId) { guildid = guildId; }

    @DynamoDbPartitionKey
    public String getGuildid()
    {
        return guildid;
    }
}
