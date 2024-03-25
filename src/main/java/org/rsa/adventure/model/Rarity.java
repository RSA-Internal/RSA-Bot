package org.rsa.adventure.model;

import lombok.Getter;

@Getter
public enum Rarity {
    COMMON("Common", "C");

    private final String name;
    private final String prefix;

    Rarity(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }
}
