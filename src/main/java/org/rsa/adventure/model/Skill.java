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

    /**
     * @param skill The skill to check.
     * @param level The users current level.
     * @return The required experience to level up.
     */
    public static int getRequiredExperienceForLevelUp(Skill skill, int level) {
        return (int) Math.floor(skill.baseExp * (Math.pow(level + 1, skill.getCurveFactor())));
    }

    public static void main(String[] args) {
        for (Skill skill : Skill.values()) {
            for (int i = 0; i < 10; i++) {
                System.out.println("Level " + (i + 1) + " " + skill.getName() + " requires " + getRequiredExperienceForLevelUp(skill, i) + " exp.");
            }
        }
    }
}
