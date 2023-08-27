package org.rsa;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.rsa.aws.SecretsManager;
import org.rsa.listeners.MessageListener;

import static org.rsa.aws.SecretsManager.getValue;

public class Bot {
    public static void main(String[] args) {
        String BOT_TOKEN = System.getenv("BOT_TOKEN");

        if (null == BOT_TOKEN) {
            BOT_TOKEN = getValue(SecretsManager.BOT_TOKEN_KEY);
        }

        JDABuilder
            .createDefault(BOT_TOKEN)
            .addEventListeners(new MessageListener())
            .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
            .build();
    }
}