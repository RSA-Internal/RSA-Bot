package org.rsa.translator;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.rsa.logic.data.models.UserAdventureProfile;
import org.rsa.util.HelperUtil;

public class AdventureProfileTranslator {

    public static MessageEmbed getAdventureProfileAsEmbed(Guild guild, UserAdventureProfile adventureProfile, Member requester, Member profileOwner) {
        return new EmbedBuilder()
            .setTitle("Inventory of: " + profileOwner.getEffectiveName())
            .setColor(HelperUtil.getRandomColor())
            .setFooter("Requested by: " + requester.getEffectiveName())
            .setThumbnail(profileOwner.getEffectiveAvatarUrl())
            .addField("Wallet", adventureProfile.getWalletAsString(), true)
            .addBlankField(true)
            .addBlankField(true)
            .addField("Backpack", adventureProfile.getBackpackAsString(), true)
            .addField("Zones", adventureProfile.getZonesAsString(), true)
            .addBlankField(true)
            .addField("Skills", adventureProfile.getSkillsAsString(), true)
            .build();
    }

}
