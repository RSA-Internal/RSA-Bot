package org.rsa.entity.adventure;

import lombok.Getter;
import org.rsa.adventure.AdventureEntities;
import org.rsa.adventure.model.Skill;
import org.rsa.entity.BaseEntity;
import org.rsa.entity.EntityManager;
import org.rsa.logic.data.models.UserAdventureProfile;

import java.util.List;

@Getter
public class SkillEntity extends BaseEntity {

    private final Integer baseExp;
    private final Double curveFactor;

    /** Used to set required level for Activities and Zones. */
    private Integer level = 0;

    public static SkillEntity fromEnum(Skill skill) {
        return new SkillEntity(skill.getId(), skill.getName(), skill.getBaseExp(), skill.getCurveFactor());
    }

    /**
     * @param skill The skill to check.
     * @param level The users current level.
     * @return The required experience to level up.
     */
    public static int getRequiredExperienceForLevelUp(SkillEntity skill, int level) {
        return (int) Math.floor(skill.getBaseExp() * (Math.pow(level + 1, skill.getCurveFactor())));
    }

    public SkillEntity(Integer id, String name, Integer baseExp, Double curveFactor) {
        super(id, name);
        this.baseExp = baseExp;
        this.curveFactor = curveFactor;
        AdventureEntities.skillManager.addEntity(this);
    }

    public SkillEntity setLevel(Integer level) {
        this.level = level;
        return this;
    }

    @Override
    public String getAsDetails() {
        return "- ID: " + getId() +
            "\n- Name: " + getName();
    }

    public List<ZoneEntity> unlockZonesOnLevelUp(UserAdventureProfile profile) {
        int userSkillLevel = profile.getSkillSetLevel().get(this.getId());
        List<Integer> unlockedZones = profile.getUnlockedZones();
        System.out.println("Checking for zones " + getName() + " - level: " + userSkillLevel);

        EntityManager<ZoneEntity> entityManager = AdventureEntities.zoneManager;

        return entityManager.getEntityList().stream()
            // Filter out unlocked zones
            .filter(zone -> !unlockedZones.contains(zone.getId()))
            // Filter out zones that don't have this skill
            .filter(zone -> zone.getRequiredSkillSet().contains(this))
            // Filter out zones where the skill is higher than the users skill level
            .filter(zone -> zone.getRequiredSkillSet().get(this.getId()).getLevel() <= userSkillLevel)
            .toList();
    }
}
