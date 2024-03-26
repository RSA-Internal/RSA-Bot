package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

    public static Skill getById(int id) {
        return skillStream().filter(skill -> skill.id.equals(id)).findFirst().orElse(null);
    }

    private static Stream<Skill> skillStream() {
        return Arrays.stream(Skill.values());
    }

    public static List<SelectOption> getSkillOptionList() {
        return getSkillOptionList(1);
    }

    public static List<SelectOption> getSkillOptionList(int defaultIndex) {
        return skillStream()
            .filter(skill -> skill.id > 0)
            .map(skill ->
                SelectOption
                    .of(skill.name, "skill-" + skill.id)
                    .withDescription("")
                    .withDefault(skill.id == defaultIndex))
            .toList();
    }

    public List<Zone> unlockZonesOnLevelUp(UserAdventureProfile profile) {
        int userSkillLevel = profile.getSkillSetLevel().get(this.getId());
        List<Integer> unlockedZones = profile.getUnlockedZones();
        System.out.println("Checking for zones " + name + " - level: " + userSkillLevel);

        return Zone.zoneStream()
            // Filter out unlocked zones
            .filter(zone -> !unlockedZones.contains(zone.getId()))
            // Filter out zones that don't have this skill
            .filter(zone -> zone.getRequiredSkills().containsKey(this))
            // Filter out zones where the skill is higher than the users skill level
            .filter(zone -> zone.getRequiredSkills().get(this) <= userSkillLevel)
            .toList();
    }

    public String getAsDetails() {
        return "- ID: " + id +
            "\n- Name: " + name;
    }
}
