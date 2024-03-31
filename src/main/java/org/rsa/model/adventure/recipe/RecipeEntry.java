package org.rsa.model.adventure.recipe;

import lombok.Getter;
import org.rsa.entity.adventure.ItemEntity;

@Getter
public class RecipeEntry {

    private final ItemEntity item;
    private final int count;

    public RecipeEntry(ItemEntity item, int count) {
        this.item = item;
        this.count = count;
    }
}
