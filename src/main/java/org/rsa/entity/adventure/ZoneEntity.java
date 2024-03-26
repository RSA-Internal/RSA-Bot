package org.rsa.entity.adventure;

import lombok.Getter;
import org.rsa.adventure.model.Skill;
import org.rsa.adventure.model.Zone;
import org.rsa.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class ZoneEntity extends BaseEntity {

    private final List<ActivityEntity> activities;
    private final List<SkillEntity> requiredSkillSet;

    public static ZoneEntity fromEnum(Zone zone) {
        Map<Skill, Integer> requiredSkillsFromZone = zone.getRequiredSkills();

        List<SkillEntity> requiredSkillEntityFromZone = new ArrayList<>();

        for (Map.Entry<Skill, Integer> skillEntry : requiredSkillsFromZone.entrySet()) {
            SkillEntity skillEntity = SkillEntity.fromEnum(skillEntry.getKey());
            skillEntity.setLevel(skillEntry.getValue());
            requiredSkillEntityFromZone.add(skillEntity);
        }

        return new ZoneEntity(
            zone.getId(),
            zone.getName(),
            zone.getActivities(),
            requiredSkillEntityFromZone);
    }

    public ZoneEntity(Integer id, String name, List<ActivityEntity> activities, List<SkillEntity> requiredSkillSet) {
        super(id, name);
        this.activities = activities;
        this.requiredSkillSet = requiredSkillSet;
    }

    @Override
    public String getAsDetails() {
        return null;
    }
}
