package org.rsa.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.awt.*;
import java.util.Random;

public class HelperUtil {

    private static final Random RANDOM = new Random();

    public static Color getColorFromProfile(Guild guild, Member member) {
        return getColorFromProfile(UserAdventureProfileManager.fetch(guild.getId(), member.getId()));
    }

    public static Color getColorFromProfile(UserAdventureProfile profile) {
        String playerColor = profile.getColorCode();
        if (playerColor == null || playerColor.isEmpty()) {
            return getRandomColor();
        }
        return Color.decode("#" + playerColor);
    }

    public static Color getRandomColor() {
        return new Color(
            RANDOM.nextInt(255),
            RANDOM.nextInt(255),
            RANDOM.nextInt(255)
        );
    }
}
