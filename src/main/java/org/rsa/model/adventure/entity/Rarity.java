package org.rsa.model.adventure.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Rarity {
    UNIQUE(1, "Unique", "U\\*"),
    FORBIDDEN(2, "Forbidden", "F\\*"),
    ALPHA(3, "Alpha", "A\\*"),
    BETA(4, "Beta", "B\\*"),
    COMMON(5, "Common", "C"),
    UNCOMMON(6, "Uncommon", "U"),
    RARE(7, "Rare", "R"),
    EPIC(8, "Epic", "E"),
    MYTHICAL(9, "Mythical", "M");

    private final Integer id;
    private final String name;
    private final String prefix;
}
