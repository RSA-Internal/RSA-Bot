package org.rsa.managers;

import net.dv8tion.jda.api.entities.Guild;
import org.rsa.aws.RequestsManager;
import org.rsa.beans.GuildConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Objects;
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

    public static String processUpdate(Guild guild, String option, String value) {
        GuildConfiguration guildConfig = GuildConfigurationManager.fetch(guild.getId());
        boolean result = false;
        if (Objects.nonNull(option) && Objects.nonNull(value)) {
            result = guildConfig.updateField(option, value);
        }

        String response = "✅ **" + option + "** option changed to " + value + ".";

        if (result) {
            GuildConfigurationManager.update(guildConfig);
        } else {
            response = "❌ **" + option + "** option was not changed to " + value + ".";
        }

        return response;
    }
}
