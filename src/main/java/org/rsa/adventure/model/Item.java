package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Item {
    NOTHING(-1, "Nothing", Rarity.ALPHA, 0, 0, false, false),
    TEST(0, "Test Item", Rarity.COMMON, 0, 0, false, false),
    BASIC_AXE(1, "Basic Axe", Rarity.COMMON, 0, 0, false, false),
    BASIC_FISHING_ROD(2, "Basic Fishing Rod", Rarity.UNCOMMON, 0, 0, false, false),
    BASIC_PICKAXE(3, "Basic Pickaxe", Rarity.COMMON, 0, 0, false, false),
    BERRY(4, "Berry", Rarity.COMMON, 0, 0, false, false),
    STICK(5, "Stick", Rarity.COMMON, 0, 0, false, false),
    PLANT_FIBER(6, "Plant Fiber", Rarity.COMMON, 0, 0, false, false),
    ROCK(7, "Rock", Rarity.COMMON, 0, 0, false, false),
    BASIC_HOE(8, "Basic Hoe", Rarity.UNCOMMON, 0, 0, false, false),
    CARROT(9, "Carrot", Rarity.COMMON, 0, 0, false, false),
    POTATO(10, "Potato", Rarity.UNCOMMON, 0, 0, false, false),
    BONE(11, "Bone", Rarity.COMMON, 0, 0, false, false),
    ANIMAL_PELT(12, "Animal Pelt", Rarity.UNCOMMON, 0, 0, false, false),
    WHEAT(13, "Wheat", Rarity.COMMON, 0, 0, false, false),
    LOG(14, "Log", Rarity.COMMON, 0, 0, false, false),
    BASIC_KNIFE(15, "Basic Knife", Rarity.COMMON, 0, 0, false, false),
    RAW_MEAT(16, "Raw Meat", Rarity.COMMON, 0, 0, false, false),
    KELP(17, "Kelp", Rarity.COMMON, 0, 0, false, false),
    RAW_FISH(18, "Raw Fish", Rarity.COMMON, 0, 0, false, false),
    ;

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

