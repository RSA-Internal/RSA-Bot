package org.rsa.translator;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.rsa.util.GuildConfigurationConstant;
import org.rsa.beans.GuildConfiguration;
import org.rsa.util.HelperUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ConfigurationTranslator {

    private static void addSectionToEmbed(Guild guild, GuildConfiguration configuration, EmbedBuilder embedBuilder, String sectionTitle, List<GuildConfigurationConstant> sectionList) {
        int index = 0;
        for (GuildConfigurationConstant constant : sectionList) {
            String title = "\u200b";
            if (index == 0) {
                title = sectionTitle;
            }
            index++;

            String constantKey = constant.getKey();
            String value = getValueDefaultOrUnset(guild, configuration, constantKey.toUpperCase(), configuration.getGetters().get(constantKey));
            if (!GuildConfigurationConstant.EMOJI_LIST_KEY.equals(sectionTitle)) {
                value = String.format("`%s`", value);
            }
            String displayValue = String.format("%s%n%s%n", constant.getLocalization(), value);

            embedBuilder.addField(title, displayValue, true);
        }

        int remaining = 3 - (index % 3);
        if (remaining < 3) {
            System.out.println("Section: " + sectionTitle + " | Remaining: " + remaining);
            for (int i = 0; i < remaining; i++) {
                embedBuilder.addBlankField(true);
            }
        }
    }

    private static EmbedBuilder getEmbedTemplate(Guild guild, Member requester) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Configuration for " + guild.getName());
        builder.setThumbnail(guild.getIconUrl());
        builder.setAuthor(requester.getEffectiveName());
        builder.setColor(HelperUtil.getRandomColor());
        return builder;
    }

    public static MessageEmbed getConfigurationAsEmbed(Guild guild, GuildConfiguration configuration, Member requester) {
        EmbedBuilder builder = getEmbedTemplate(guild, requester);
        Map<String, List<GuildConfigurationConstant>> configurationConstants = GuildConfigurationConstant.LIST;
        configurationConstants.forEach((key, value) -> addSectionToEmbed(guild, configuration, builder, key, value));

        return builder.build();
    }

    public static MessageEmbed getConfigurationListAsEmbed(Guild guild, GuildConfiguration configuration, Member requester, String listKey) {
        EmbedBuilder builder = getEmbedTemplate(guild, requester);
        Map<String, List<GuildConfigurationConstant>> configurationConstants = GuildConfigurationConstant.LIST;
        addSectionToEmbed(guild, configuration, builder, getTitleFromKey(configurationConstants, listKey), getListFromKey(configurationConstants, listKey));

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

    private static String getTitleFromKey(Map<String, List<GuildConfigurationConstant>> constants, String listKey) {
        Optional<String> foundKey = constants.keySet().stream().filter(key -> key.toLowerCase().equals(listKey)).findFirst();
        return foundKey.orElse(null);
    }

    private static List<GuildConfigurationConstant> getListFromKey(Map<String, List<GuildConfigurationConstant>> constants, String listKey) {
        Optional<String> foundKey = constants.keySet().stream().filter(key -> key.toLowerCase().equals(listKey)).findFirst();
        return foundKey.map(constants::get).orElse(null);
    }
}
