package org.rsa.logic.data.models;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@Getter
@Setter
public class GuildConfiguration {
    Integer schema_version = 2;
    String guildid; // Partition key

    // Emojis
    String upvote_emoji = "";
    String downvote_emoji = "";
    String moderate_emoji = "";

    // Role IDs
    String moderator_role_id = "";
    String helper_role_id = "";

    // Channel IDs
    String help_channel_id = "";

    // Options
    String req_chars_for_help_thread = "";
    String help_thread_title_length = "";

    public GuildConfiguration() { }
    public GuildConfiguration(String guildId) { guildid = guildId; }

    @DynamoDbPartitionKey
    public String getGuildid()
    {
        return guildid;
    }
}
