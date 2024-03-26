package org.rsa.entity.adventure;

import lombok.Getter;
import org.rsa.adventure.model.Rarity;
import org.rsa.entity.BaseEntity;

@Getter
public class RarityEntity extends BaseEntity {

    private final String prefix;

    public static RarityEntity fromEnum(Rarity rarity) {
        return new RarityEntity(rarity.getId(), rarity.getName(), rarity.getPrefix());
    }

    public RarityEntity(Integer id, String name, String prefix) {
        super(id, name);
        this.prefix = prefix;
    }

    @Override
    public String getAsDetails() {
        return "- ID: " + getId() + "\n- Name: " + getName() + "\n- Prefix: " + prefix;
    }
}
