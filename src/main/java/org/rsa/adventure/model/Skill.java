package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Skill {
    NO_SKILL(0, "Nothing", 0, 0.0),
    HUNTING(1, "Hunting", 25, 1.1),
    FORAGING(2, "Foraging", 30, 1.2),
    FISHING(3, "Fishing", 50, 1.3),
    MINING(4, "Mining", 30, 1.1),
    COOKING(5, "Cooking", 15, 1.2),
    CRAFTING(6, "Crafting", 15, 1.2),
    FARMING(7, "Farming", 25, 1.4),
    ;

    private final Integer id;
    private final String name;
    private final Integer baseExp;
    private final Double curveFactor;
}
