package org.rsa.util;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.utils.ImageProxy;
import org.rsa.aws.accessor.S3Accessor;
import org.rsa.aws.factory.DependencyFactory;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Slf4j
public class BackupUtil {

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH_mm_ss";
    private static final S3Client s3Client = DependencyFactory.s3Client();

    public static void backupGuild(Guild guild) {
        backupEmojis(guild);
        backupStickers(guild);
        backupServerIcon(guild);
    }

    private static void backupEmojis(Guild guild) {
        guild.getEmojis().forEach(e -> backupEmoji(guild, e));
    }

    public static void backupEmoji(Guild guild, RichCustomEmoji emoji) {
        if (Objects.isNull(guild) || Objects.isNull(emoji)) return;
        try {
            String fileName = emoji.getName() + "_" + emoji.getId() + ".png";
            File f = emoji.getImage().downloadToFile(new File(fileName)).get();
            S3Accessor.createResource(s3Client, guild.getId() + "-emojis", fileName, f.getPath());
            cleanupResource(f.getPath());
        } catch (ExecutionException | InterruptedException e) {
            log.warn("Failed to download emoji: " + emoji.getName());
        }
    }

    private static void backupStickers(Guild guild) {
        guild.getStickers().forEach(s -> backupSticker(guild, s));
    }

    public static void backupSticker(Guild guild, Sticker sticker) {
        if (Objects.isNull(guild) || Objects.isNull(sticker)) return;
        try {
            String fileName = sticker.getName() + "_" + sticker.getId() + ".png";
            File f = sticker.getIcon().downloadToFile(new File(fileName)).get();
            S3Accessor.createResource(s3Client, guild.getId() + "-stickers", fileName, f.getPath());
            cleanupResource(fileName);
        } catch (ExecutionException | InterruptedException e) {
            log.warn("Failed to download emoji: " + sticker.getName());
        }
    }

    public static void backupServerIcon(Guild guild) {
        if (Objects.isNull(guild)) return;
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_NOW);
            String datePrefix = format.format(cal.getTime());
            String fileName = datePrefix + "_server_icon.png";
            ImageProxy guildIcon = guild.getIcon();
            if (Objects.nonNull(guildIcon)) {
                File f = guildIcon.downloadToFile(new File(fileName)).get();
                S3Accessor.createResource(s3Client, guild.getId() + "-servericon", fileName, f.getPath());
                cleanupResource(fileName);
            }
        } catch (ExecutionException | InterruptedException e) {
            log.warn("Failed to download guild icon");
        }
    }

    private static void cleanupResource(String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            boolean deleted = f.delete();
            if (deleted) {
                log.info("Successfully deleted: " + filePath);
            } else {
                log.warn("Failed to delete: " + filePath);
            }
        } else {
            log.info("No resource to delete: " + filePath);
        }
    }
}
