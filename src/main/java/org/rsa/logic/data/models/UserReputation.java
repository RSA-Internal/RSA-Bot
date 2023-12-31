package org.rsa.logic.data.models;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@Getter
@Setter
public class UserReputation
{
    Integer schema_version = 1;
    String guildid;
    String userid;

    Integer given_post_upvotes = 0;
    Integer given_post_downvotes = 0;
    Integer given_moderations = 0;
    Integer given_spam_flags = 0;

    Integer received_post_upvotes = 0;
    Integer received_post_downvotes = 0;
    Integer received_moderations = 0;
    Integer received_spam_flags = 0;

    Integer accepted_answers = 0;
    Integer other_answers_accepted = 0;
    Integer reputation = 0;

    public UserReputation() {}
    public UserReputation(String guildId, String userId) {
        this.guildid = guildId;
        this.userid = userId;
    }

    @DynamoDbSortKey
    public String getUserid()
    {
        return userid;
    }

    @DynamoDbPartitionKey
    public String getGuildid()
    {
        return guildid;
    }
}
