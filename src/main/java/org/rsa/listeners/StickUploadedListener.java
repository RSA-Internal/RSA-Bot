package org.rsa.listeners;

import net.dv8tion.jda.api.events.sticker.GuildStickerAddedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static org.rsa.util.BackupUtil.backupSticker;

public class StickUploadedListener extends ListenerAdapter {
    @Override
    public void onGuildStickerAdded(GuildStickerAddedEvent event) {
        backupSticker(event.getGuild(), event.getSticker());
    }
}
