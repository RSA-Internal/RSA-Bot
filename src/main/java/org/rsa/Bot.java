package org.rsa;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.rsa.aws.SecretsManager;
import org.rsa.command.CommandObject;
import org.rsa.command.Commands;
import org.rsa.listeners.AutoCompleteListener;
import org.rsa.listeners.SlashCommandListener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.rsa.aws.SecretsManager.getValue;

public class Bot {
    public static void main(String[] args) throws InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(getBotToken());

        configureMemoryUsage(builder);
        builder.addEventListeners(
                new AutoCompleteListener(),
                new SlashCommandListener()
        );
        builder.addEventListeners(new ListenerAdapter() {
            @Override
            public void onGuildJoin(@NotNull GuildJoinEvent event) {
                super.onGuildJoin(event);
                setupGuild(event.getGuild());
            }
        });

        JDA jda = builder.build();
        jda.awaitReady();

        jda.getGuilds().forEach(guild -> {
            if (guild == null) {
                System.err.println("Failed to fetch guild");
            } else {
                setupGuild(guild);
            }
        });
    }

    private static String getBotToken() {
        String botToken = System.getenv("BOT_TOKEN");

        if (null == botToken) {
            botToken = getValue(SecretsManager.BOT_TOKEN_KEY);
        }

        return botToken;
    }

    private static void configureMemoryUsage(JDABuilder builder) {
        builder
                // Enable the bulk delete event.
                .setBulkDeleteSplittingEnabled(false)
                // Disable All CacheFlags.
                .disableCache(Arrays.asList(CacheFlag.values()))
                // Enable specific CacheFlags
                .enableCache(CacheFlag.EMOJI, CacheFlag.CLIENT_STATUS)
                // Only cache members who are online or owner of the guild.
                .setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.OWNER))
                // Disable member chunking on startup.
                .setChunkingFilter(ChunkingFilter.NONE)
                // Disable All intents
                .disableIntents(Arrays.asList(GatewayIntent.values()))
                // Enable specific intents.
                .enableIntents(
                        GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_PRESENCES
                )
                // Consider guilds with more than 50 members as "large".
                // Large guilds will only provide online members in the setup and thus reduce
                // bandwidth if chunking is disabled.
                .setLargeThreshold(50)
                // Set Activity to display the version.
                .setActivity(Activity.playing("v0.1"));
    }

    private static void setupGuild(Guild guild) {
        System.out.println("Setting up commands for: " + guild.getId());
        CommandListUpdateAction commands = guild.updateCommands();

        List<CommandObject> commandObjectList = Commands.getCommands();
        List<SlashCommandData> slashCommandData = commandObjectList.stream()
                .map(CommandObject::slashCommandImplementation)
                .collect(Collectors.toList());

        commands.addCommands(slashCommandData).queue();
    }
}