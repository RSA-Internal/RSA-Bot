package org.rsa.entity.adventure;

import lombok.Getter;
import org.rsa.register.adventure.EntityManagerRegister;
import org.rsa.model.adventure.entity.Skill;
import org.rsa.model.adventure.entity.Zone;
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
        EntityManagerRegister.zoneManager.addEntity(this);
    }

    @Override
    public String getAsDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n- ID: ");
        builder.append(getId());
        builder.append("\n- Name: ");
        builder.append(getName());
        builder.append("\n- Activities");
        for (ActivityEntity activity : activities) {
            builder.append("\n - ");
            builder.append(activity.getName());
        }
        builder.append("\n- Unlock Requirements");
        List<SkillEntity> skillsRequiredAboveZero = requiredSkillSet.stream().filter(skill -> skill.getLevel() > 0).toList();
        if (!skillsRequiredAboveZero.isEmpty()) {
            for (SkillEntity skill : skillsRequiredAboveZero) {
                builder.append("\n - ");
                builder.append(skill.getName());
                builder.append(" [Level ");
                builder.append(skill.getLevel());
                builder.append("]");
            }
        } else {
            builder.append("\n - None");
        }

        return builder.toString();
    }
}
