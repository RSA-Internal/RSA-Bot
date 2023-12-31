package org.rsa.logic.data.managers;

import org.rsa.aws.RequestsManager;
import org.rsa.logic.data.models.GuildConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Optional;

public class GuildConfigurationManager {
    private final static String TABLE_NAME = "guild_configuration_data";
    private final static RequestsManager<GuildConfiguration> _requestsManager = new RequestsManager<>(TABLE_NAME, GuildConfiguration.class);

    public static GuildConfiguration fetch(String guildid)
    {
        QueryConditional query = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(guildid)
                        .build());
        Optional<Page<GuildConfiguration>> optionalPage = _requestsManager.fetchSingleItem(query);

        if (optionalPage.isEmpty() || optionalPage.get().items().isEmpty()) return new GuildConfiguration(guildid);
        return optionalPage.get().items().get(0);
    }

    public static void update(GuildConfiguration item)
    {
        _requestsManager.enqueueItemUpdate(item);
    }
}
