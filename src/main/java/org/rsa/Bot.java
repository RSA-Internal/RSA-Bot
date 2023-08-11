package org.rsa;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.rsa.listeners.MessageListener;

public class Bot {
    public static void main(String[] args) {
        final String BOT_TOKEN = System.getenv("BOT_TOKEN");

        JDA api = JDABuilder
                .createDefault(BOT_TOKEN)
                .addEventListeners(new MessageListener())
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
                .build();
    }
}