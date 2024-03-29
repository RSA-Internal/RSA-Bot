package org.rsa.model.adventure.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rsa.entity.adventure.ItemEntity;
import org.rsa.entity.loot.LootTable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.rsa.entity.loot.LootTable.LootTableBuilder;

@Getter
@AllArgsConstructor
public enum Activity {
    LEAVE(0, "Leave", 0, 0, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap()),
    HUNT(1, "Hunt Animals",  5, 2,
        List.of(Item.BASIC_KNIFE),
        List.of(
            new LootTableBuilder()
                .withLootTableEntry(ItemEntity.fromEnum(Item.ANIMAL_PELT), 1, 1, 1)
                .withLootTableEntry(ItemEntity.fromEnum(Item.BONE), 4, 1, 2)
                .withLootTableEntry(ItemEntity.fromEnum(Item.RAW_MEAT), 8, 1, 4)
                .withLootTableEntry(ItemEntity.fromEnum(Item.NOTHING), 10, 1, 1)
                .build()
        ),
        Map.of(Skill.HUNTING, 0)),
    FORAGE(2, "Forage", 3, 3,
        List.of(),
        List.of(
            new LootTableBuilder()
                .withLootTableEntry(ItemEntity.fromEnum(Item.ROCK), 2, 1, 1)
                .withLootTableEntry(ItemEntity.fromEnum(Item.STICK), 3, 1, 2)
                .withLootTableEntry(ItemEntity.fromEnum(Item.BERRY), 5, 1, 3)
                .withLootTableEntry(ItemEntity.fromEnum(Item.PLANT_FIBER), 6, 1, 4)
                .withLootTableEntry(ItemEntity.fromEnum(Item.NOTHING), 10, 1, 1)
                .build()
        ),
        Map.of(Skill.FORAGING, 0)),
    FISH(3, "Fish",  2, 1,
        List.of(Item.BASIC_FISHING_ROD),
        List.of(
            new LootTableBuilder()
                .withLootTableEntry(ItemEntity.fromEnum(Item.BONE), 1, 1, 1)
                .withLootTableEntry(ItemEntity.fromEnum(Item.RAW_FISH), 3, 1, 1)
                .withLootTableEntry(ItemEntity.fromEnum(Item.KELP), 4, 1, 2)
                .withLootTableEntry(ItemEntity.fromEnum(Item.STICK), 5, 1, 1)
                .withLootTableEntry(ItemEntity.fromEnum(Item.NOTHING), 10, 1, 1)
                .build()
        ),
        Map.of(Skill.FISHING, 0)),
    MINE(5, "Mine", 5, 2,
        List.of(Item.BASIC_PICKAXE),
        List.of(
            new LootTableBuilder()
                .withLootTableEntry(ItemEntity.fromEnum(Item.ROCK), 1, 1, 4)
                .build()
        ),
        Map.of(Skill.MINING, 0)),
    FARM(6, "Farm", 3, 3,
        List.of(Item.BASIC_HOE),
        List.of(
            new LootTableBuilder()
                .withLootTableEntry(ItemEntity.fromEnum(Item.PLANT_FIBER), 2, 1, 2)
                .withLootTableEntry(ItemEntity.fromEnum(Item.POTATO), 2, 1, 3)
                .withLootTableEntry(ItemEntity.fromEnum(Item.ROCK), 3, 1, 2)
                .withLootTableEntry(ItemEntity.fromEnum(Item.BERRY), 4, 1, 2)
                .withLootTableEntry(ItemEntity.fromEnum(Item.CARROT), 4, 1, 4)
                .withLootTableEntry(ItemEntity.fromEnum(Item.NOTHING), 10, 1, 1)
                .build()
        ),
        Map.of(Skill.FORAGING, 2)),
    CHOP(7, "Chop Tree", 4, 1,
        List.of(Item.BASIC_AXE),
        List.of(
            new LootTableBuilder()
                .withLootTableEntry(ItemEntity.fromEnum(Item.LOG), 1, 1, 2)
                .build()
        ),
        Map.of(Skill.FORAGING, 1, Skill.LOGGING, 0)),
    ;

    private final Integer id;
    private final String name;
    private final Integer experienceGainBound;
    private final Integer rewardRolls;
    private final List<Item> requiredItems;
    private final List<LootTable> lootTables;
    private final Map<Skill, Integer> requiredSkillSet;
}