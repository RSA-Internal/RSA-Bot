package org.rsa.model.adventure.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rsa.entity.adventure.ItemEntity;
import org.rsa.model.adventure.entity.Item;

import java.util.List;

@AllArgsConstructor
@Getter
public enum Recipe {
    RECIPE_BASIC_AXE(1, "Basic Axe",
        List.of(
            new RecipeEntry(ItemEntity.fromEnum(Item.STICK), 2),
            new RecipeEntry(ItemEntity.fromEnum(Item.PLANT_FIBER), 4),
            new RecipeEntry(ItemEntity.fromEnum(Item.ROCK), 3)
        ),
        List.of(
            new RecipeEntry(ItemEntity.fromEnum(Item.BASIC_AXE), 1)
        )
    ),
    RECIPE_BASIC_FISHING_ROD(2, "Basic Fishing Rod",
        List.of(
            new RecipeEntry(ItemEntity.fromEnum(Item.STICK), 4),
            new RecipeEntry(ItemEntity.fromEnum(Item.PLANT_FIBER), 6),
            new RecipeEntry(ItemEntity.fromEnum(Item.BONE), 1)
        ),
        List.of(
            new RecipeEntry(ItemEntity.fromEnum(Item.BASIC_FISHING_ROD), 1)
        )
    ),
    RECIPE_BASIC_PICKAXE(3, "Basic Pickaxe",
        List.of(
            new RecipeEntry(ItemEntity.fromEnum(Item.STICK), 3),
            new RecipeEntry(ItemEntity.fromEnum(Item.PLANT_FIBER), 5),
            new RecipeEntry(ItemEntity.fromEnum(Item.ROCK), 4)
        ),
        List.of(
            new RecipeEntry(ItemEntity.fromEnum(Item.BASIC_PICKAXE), 1)
        )
    ),
    RECIPE_BASIC_HOE(4, "Basic Hoe",
        List.of(
            new RecipeEntry(ItemEntity.fromEnum(Item.STICK), 3),
            new RecipeEntry(ItemEntity.fromEnum(Item.PLANT_FIBER), 3),
            new RecipeEntry(ItemEntity.fromEnum(Item.ROCK), 2)
        ),
        List.of(
            new RecipeEntry(ItemEntity.fromEnum(Item.BASIC_HOE), 1)
        )
    ),
    RECIPE_BASIC_KNIFE(5, "Basic Knife",
        List.of(
            new RecipeEntry(ItemEntity.fromEnum(Item.ROCK), 1),
            new RecipeEntry(ItemEntity.fromEnum(Item.BONE), 2)
        ),
        List.of(
            new RecipeEntry(ItemEntity.fromEnum(Item.BASIC_KNIFE), 1)
        )
    )
    ;

    private final int id;
    private final String name;
    private final List<RecipeEntry> inputs;
    private final List<RecipeEntry> outputs;
}
