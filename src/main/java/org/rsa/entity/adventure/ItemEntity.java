package org.rsa.entity.adventure;

import lombok.Getter;
import org.rsa.entity.BaseEntity;
import org.rsa.model.adventure.entity.Item;
import org.rsa.model.adventure.entity.Rarity;
import org.rsa.register.adventure.EntityManagerRegister;

@Getter
public class ItemEntity extends BaseEntity {

    private final Rarity rarity;
    private final Integer buyPrice;
    private final Integer sellPrice;
    private final Integer durability;
    private final Integer quantity;
    private final boolean canTrade;
    private final boolean canDrop;

    public static ItemEntity fromEnum(Item item) {
        return new ItemEntity(
            item.getId(),
            item.getName(),
            item.getRarity(),
            item.getBuyPrice(),
            item.getSellPrice(),
            item.isCanTrade(),
            item.isCanDrop(),
            -1,
            0
        );
    }

    public ItemEntity(Integer id, String name, Rarity rarity, Integer buyPrice, Integer sellPrice, boolean canTrade, boolean canDrop, int durability, int quantity) {
        super(id, name);
        this.rarity = rarity;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.canTrade = canTrade;
        this.canDrop = canDrop;
        this.durability = durability;
        this.quantity = quantity;
        EntityManagerRegister.itemManager.addEntity(this);
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
