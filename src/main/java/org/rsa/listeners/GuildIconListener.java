package org.rsa.listeners;

import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static org.rsa.util.BackupUtil.backupServerIcon;

public class GuildIconListener extends ListenerAdapter {

    @Override
    public void onGuildUpdateIcon(GuildUpdateIconEvent event) {
        backupServerIcon(event.getGuild());
    }
}
