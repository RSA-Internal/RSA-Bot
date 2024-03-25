package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Item {
    TEST(0, "Test Item", Rarity.COMMON, 0, 0, false, false);

    private final Integer id;
    private final String name;
    private final Rarity rarity;
    private final Integer buyPrice;
    private final Integer sellPrice;
    private final boolean canTrade;
    private final boolean canDrop;

    private static Stream<Item> itemStream() {
        return Arrays.stream(Item.values());
    }

    public static Item getById(int id) {
        return itemStream().filter(item -> item.id.equals(id)).findFirst().orElse(null);
    }

    public static List<Item> getByRarity(Rarity rarity) {
        return itemStream().filter(item -> item.rarity.equals(rarity)).toList();
    }
}

