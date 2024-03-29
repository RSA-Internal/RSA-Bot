package org.rsa.model.adventure.loot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.rsa.entity.adventure.ItemEntity;

@Getter
@AllArgsConstructor
public class LootTableEntry {
    private ItemEntity itemToDrop;
    private int weight;
    private int minDrop;
    private int maxDrop;
}
