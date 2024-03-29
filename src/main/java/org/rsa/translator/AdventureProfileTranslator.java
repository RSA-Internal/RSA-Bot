package org.rsa.translator;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.rsa.model.adventure.Currency;
import org.rsa.logic.data.models.UserAdventureProfile;

import static org.rsa.util.EmbedBuilderUtil.getEmbedBuilderTemplate;

public class AdventureProfileTranslator {

    public static MessageEmbed getAdventureProfileAsEmbed(Guild guild, UserAdventureProfile adventureProfile, Member requester, Member profileOwner) {
        return getEmbedBuilderTemplate(guild, requester, "Inventory of: " + profileOwner.getEffectiveName(), profileOwner)
            .addField("Wallet", adventureProfile.getWalletAsString(), true)
            .addBlankField(true)
            .addBlankField(true)
            .addField("Backpack [" + adventureProfile.getBackpackValue() + " " + Currency.PANDA_COIN.getEmojiId() + " ]", adventureProfile.getBackpackAsString(), true)
            .addField("Zones", adventureProfile.getZonesAsString(), true)
            .addBlankField(true)
            .addField("Skills", adventureProfile.getSkillsAsString(), true)
            .build();
    }

}
