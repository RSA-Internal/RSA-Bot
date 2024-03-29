package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Item {
    NOTHING(-1, "Nothing", Rarity.FORBIDDEN, 0, 0, false, false),
    TEST(0, "Test Item", Rarity.UNIQUE, 0, 0, false, false),
    GIOS_ROSE(1, "Gio's Rose", Rarity.ALPHA, 0, 0, false, false),
    FEMBOY(2, "Femboy", Rarity.ALPHA, 0, 0, false, false),
    SCALA_MANIFESTO(3, "Scala Manifesto", Rarity.ALPHA, 0, 0, false, false),
    PLANET(4, "Planet", Rarity.ALPHA, 0, 0, false, false),
    PADLOCK(5, "Pad Lock VI", Rarity.ALPHA, 0, 0, false, false),
    BASIC_AXE(6, "Basic Axe", Rarity.COMMON, 0, 0, false, false),
    BASIC_FISHING_ROD(7, "Basic Fishing Rod", Rarity.UNCOMMON, 0, 0, false, false),
    BASIC_PICKAXE(8, "Basic Pickaxe", Rarity.COMMON, 0, 0, false, false),
    BASIC_HOE(9, "Basic Hoe", Rarity.UNCOMMON, 0, 0, false, false),
    BASIC_KNIFE(10, "Basic Knife", Rarity.COMMON, 0, 0, false, false),
    BERRY(11, "Berry", Rarity.COMMON, 0, 1, true, true),
    STICK(12, "Stick", Rarity.COMMON, 0, 1, true, true),
    PLANT_FIBER(13, "Plant Fiber", Rarity.COMMON, 1, 0, true, true),
    ROCK(14, "Rock", Rarity.COMMON, 0, 1, true, true),
    CARROT(15, "Carrot", Rarity.COMMON, 0, 3, true, true),
    POTATO(16, "Potato", Rarity.UNCOMMON, 0, 2, true, true),
    BAKED_POTATO(17, "Baked Potato", Rarity.UNCOMMON, 5, 0, true, true),
    BONE(18, "Bone", Rarity.COMMON, 0, 2, true, true),
    ANIMAL_PELT(19, "Animal Pelt", Rarity.UNCOMMON, 10, 0, true, true),
    WHEAT(20, "Wheat", Rarity.COMMON, 0, 2, true, true),
    FLOUR(21, "Flour", Rarity.COMMON, 0, 3, true, true),
    LOG(22, "Log", Rarity.COMMON, 0, 1, true, true),
    RAW_MEAT(23, "Raw Meat", Rarity.COMMON, 0, 2, true, true),
    COOKED_MEAT(24, "Cooked Meat", Rarity.COMMON, 0, 6, true, true),
    KELP(25, "Kelp", Rarity.COMMON, 0, 1, true, true),
    RAW_FISH(26, "Raw Fish", Rarity.COMMON, 0, 1, true, true),
    COOKED_FISH(27, "Cooked Fish", Rarity.COMMON, 0, 3, true, true),
    ;

    private final Integer id;
    private final String name;
    private final Rarity rarity;
    private final Integer buyPrice;
    private final Integer sellPrice;
    private final boolean canTrade;
    private final boolean canDrop;
}

