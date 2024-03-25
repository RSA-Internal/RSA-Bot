package org.rsa.adventure.model;

import lombok.Getter;

@Getter
public enum Rarity {
    COMMON("Common", "C"),
    ALPHA("Alpha", "A*"),
    BETA("Beta", "B*"),
    UNCOMMON("Uncommon", "U"),
    RARE("Rare", "R"),
    EPIC("Epic", "E"),
    MYTHICAL("Mythical", "M");

    private final String name;
    private final String prefix;

    Rarity(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }
}
