package org.rsa.translator;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.rsa.logic.data.models.GuildConfiguration;
import org.rsa.util.HelperUtil;

import java.util.Map;
import java.util.function.Supplier;

public class ConfigurationTranslator {

    public static MessageEmbed getConfigurationAsEmbed(Guild guild, GuildConfiguration configuration, Member requester) {
        Map<String, Supplier<String>> getters = configuration.getGetters();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Configuration for " + guild.getName());
        builder.setThumbnail(guild.getIconUrl());
        builder.setAuthor(requester.getEffectiveName());
        builder.setColor(HelperUtil.getRandomColor());

        for (Map.Entry<String, Supplier<String>> entry : getters.entrySet()) {
            String key = entry.getKey();
            String value = getValueDefaultOrUnset(guild, configuration, key, entry.getValue());
            builder.addField(key, value, true);
        }

        return builder.build();
    }

    private static String getValueDefaultOrUnset(Guild guild, GuildConfiguration configuration, String fieldName, Supplier<String> getter) {
        String defaultValue = getValueOrDefault(getter, configuration.setDefault(guild, fieldName));
        return getValueOrDefault(defaultValue, "--unset--");
    }

    private static String getValueOrDefault(Supplier<String> getter, String defaultValue) {
        return getValueOrDefault(getter.get(), defaultValue);
    }

    private static String getValueOrDefault(String original, String defaultValue) {
        if (original.isBlank() || original.isEmpty()) {
            original = defaultValue;
        }
        return original;
    }
}
