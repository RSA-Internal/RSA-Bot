package org.rsa.listeners;

import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static org.rsa.util.BackupUtil.backupEmoji;

public class EmojiUploadedListener extends ListenerAdapter {
    @Override
    public void onEmojiAdded(@NotNull EmojiAddedEvent event) {
        backupEmoji(event.getGuild(), event.getEmoji());
    }
}
