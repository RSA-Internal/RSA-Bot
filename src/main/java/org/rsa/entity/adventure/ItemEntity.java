package org.rsa.entity.adventure;

import lombok.Getter;
import org.rsa.adventure.model.Item;
import org.rsa.adventure.model.ItemDrop;
import org.rsa.adventure.model.Rarity;
import org.rsa.entity.BaseEntity;

@Getter
public class ItemEntity extends BaseEntity {

    private final Rarity rarity;
    private final Integer buyPrice;
    private final Integer sellPrice;
    private final boolean canTrade;
    private final boolean canDrop;

    private ItemDrop itemDrop;

    public static ItemEntity fromEnum(Item item) {
        return new ItemEntity(
            item.getId(),
            item.getName(),
            item.getRarity(),
            item.getBuyPrice(),
            item.getSellPrice(),
            item.isCanTrade(),
            item.isCanDrop()
        );
    }

    public ItemEntity(Integer id, String name, Rarity rarity, Integer buyPrice, Integer sellPrice, boolean canTrade, boolean canDrop) {
        super(id, name);
        this.rarity = rarity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.canTrade = canTrade;
        this.canDrop = canDrop;
    }

    public ItemEntity setItemDrop(ItemDrop itemDrop) {
        this.itemDrop = itemDrop;
        return this;
    }

    @Override
    public String getAsDetails() {
        return "- ID: " + getId() +
            "\n- Name: " + getName() +
            "\n- Rarity: " + rarity +
            "\n- Buy: " + buyPrice + " | Sell: " + sellPrice +
            "\n- Tradable: " + canTrade +
            "\n- Droppable: " + canDrop;
    }
}
