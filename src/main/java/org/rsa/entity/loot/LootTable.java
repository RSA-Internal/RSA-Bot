package org.rsa.entity.loot;

import lombok.Getter;
import org.rsa.entity.adventure.ItemEntity;
import org.rsa.model.adventure.entity.Item;
import org.rsa.model.adventure.loot.LootTableEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class LootTable {

    private static final Logger logger = LoggerFactory.getLogger(LootTable.class);

    private final Random random = new Random();
    private final List<LootTableEntry> entries;
    private final Map<Integer, Double> lookupTable;
    private final int weightSum;

    private LootTable(List<LootTableEntry> entries, Map<Integer, Double> lookupTable, int weightSum) {
        logger.info("Generating table with total weight: {}", weightSum);
        this.entries = entries;
        this.lookupTable = lookupTable;
        this.weightSum = weightSum;
    }

    public LootTableEntry getDrop() {
        logger.info("Getting drop from table.");
        int entryPick = random.nextInt(weightSum) + 1;
        logger.info("Entry pick: {}", entryPick);

        for (LootTableEntry entry : entries) {
            logger.info("Checking entry {} - weight: {}", entry.getItemToDrop().getName(), entry.getWeight());
            int value = entry.getWeight();
            if (entryPick > value) {
                entryPick -= value;
            } else {
                return entry;
            }
            logger.info("remaining entry pick: {}", entryPick);
        }

        return null;
    }

    public Double getWeightPercentage(ItemEntity item) {
        double val = lookupTable.get(item.getId());
        BigDecimal bd = new BigDecimal(val);
        bd = bd.round(new MathContext(3));
        return bd.doubleValue();
    }

    public String generateDisplay(boolean depth, boolean includeNothing) {
        return entries.stream()
            .filter(entry -> {
                if (Item.NOTHING.getId().equals(entry.getItemToDrop().getId())) {
                    return includeNothing;
                }
                return true;
            })
            .map(entry -> {
                ItemEntity item = entry.getItemToDrop();
                StringBuilder builder = new StringBuilder();
                if (depth) builder.append(" ");
                builder.append("- ");
                if (!Item.NOTHING.getId().equals(item.getId())) {
                    builder.append(entry.getMinDrop());
                    if (entry.getMaxDrop() > entry.getMinDrop()) {
                        builder.append(" - ");
                        builder.append(entry.getMaxDrop());
                    }
                    builder.append(" ");
                }
                builder.append(item.getName());
                builder.append(" (1/");
                builder.append((int) Math.floor(((double) 1 / getWeightPercentage(item)) * 100));
                builder.append(")");
//                builder.append(" (");
//                builder.append(getWeightPercentage(item));
//                builder.append("%)");

                return builder.toString();
            })
            .collect(Collectors.joining("\n"));
    }

    public static class LootTableBuilder {
        private final List<LootTableEntry> entries = new ArrayList<>();

        public LootTableBuilder withLootTableEntry(LootTableEntry lootTableEntry) {
            entries.add(lootTableEntry);
            return this;
        }

        public LootTableBuilder withLootTableEntry(ItemEntity item, int weight, int minDrop, int maxDrop) {
            return withLootTableEntry(new LootTableEntry(item, weight, minDrop, maxDrop));
        }

        public LootTable build() {
            Map<Integer, Double> lookupTable = new HashMap<>();

            int weightSum = 0;
            for (LootTableEntry entry : entries) {
                weightSum += entry.getWeight();
            }

            for (LootTableEntry entry : entries) {
                double percentage = ((double) entry.getWeight() / weightSum) * 100;
                logger.info("Writing weight percentage as {} - Item {} @ weight {} / total {}", percentage, entry.getItemToDrop().getName(), entry.getWeight(), weightSum);
                lookupTable.put(entry.getItemToDrop().getId(), percentage);
            }

            return new LootTable(entries, lookupTable, weightSum);
        }
    }
}
