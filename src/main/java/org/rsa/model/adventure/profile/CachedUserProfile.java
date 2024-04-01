package org.rsa.model.adventure.profile;

import org.rsa.entity.adventure.*;
import org.rsa.model.adventure.entity.Rarity;
import org.rsa.register.adventure.EntityManagerRegister;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedUserProfile {
    /* This value should match the version in SavedUserProfile. */
    Integer schema_version = 1;

    String guildId;
    String userId;

    List<CurrencyEntity> wallet;
    Map<String, ItemEntity> backpack;
    Map<Integer, ItemEntity> equipment;
    Map<Integer, SkillEntity> skills;
    List<ZoneEntity> unlockedZones;

    // TODO: Replace with List<Achievement>
    List<Integer> achievements;
    Map<Integer, BigInteger> activitiesPerformed;

    // TODO: Replace with List<UserOption>
    Map<String, String> userOptions;

    public CachedUserProfile(SavedUserProfile savedUserProfile) {
        this.guildId = savedUserProfile.guildId;
        this.userId = savedUserProfile.userId;
        this.wallet = new ArrayList<>();

        for (Map.Entry<Integer, BigInteger> walletEntry : savedUserProfile.wallet.entrySet()) {
            CurrencyEntity currency = EntityManagerRegister.currencyManager.getEntityById(walletEntry.getKey());
            this.wallet.add(new CurrencyEntity(currency.getId(), currency.getName(), walletEntry.getValue().intValue()));
        }

        this.backpack = new HashMap<>();

        for (Map.Entry<String, Map<String, String>> backpackEntry : savedUserProfile.backpack.entrySet()) {
            String[] itemKeyParts = backpackEntry.getKey().split("_");
            Map<String, String> itemData = backpackEntry.getValue();

            int itemId = Integer.parseInt(itemKeyParts[0]);
            int itemRarityId = Integer.parseInt(itemKeyParts[1]);

            ItemEntity itemEntity = EntityManagerRegister.itemManager.getEntityById(itemId);
            RarityEntity rarity = EntityManagerRegister.rarityManager.getEntityById(itemRarityId);
            ItemEntity newItem = new ItemEntity(
                itemId, itemEntity.getName(),
                Rarity.valueOf(rarity.getName().toUpperCase()),
                itemEntity.getBuyPrice(), itemEntity.getSellPrice(),
                itemEntity.isCanTrade(), itemEntity.isCanDrop(),
                Integer.parseInt(itemData.get("durability")),
                Integer.parseInt(itemData.get("quantity"))
            );
            this.backpack.put(backpackEntry.getKey(), newItem);
        }
    }

    public CachedUserProfile(String guildId, String userId) {

    }
}
