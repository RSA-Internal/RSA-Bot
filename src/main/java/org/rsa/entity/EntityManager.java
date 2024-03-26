package org.rsa.entity;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.rsa.adventure.model.Rarity;
import org.rsa.entity.adventure.ItemEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EntityManager<T extends BaseEntity> {

    private final Logger logger = LoggerFactory.getLogger(EntityManager.class);
    private final Class<T> classInstance;
    private final List<T> entityList = new ArrayList<>();

    public EntityManager(Class<T> clazz) {
        classInstance = clazz;
    }

    public int getNextFreeId() {
        return entityList.stream().map(BaseEntity::getId).max(Comparator.naturalOrder()).orElse(entityList.size()) + 1;
    }

    public void addEntity(T entity) {
        logger.info("Adding entity: [" + entity.getId() + "] " + entity.getName() + ".");
        T existingEntity = getEntityById(entity.getId());
        if (existingEntity != null) {
            if (!existingEntity.equals(entity)) {
                logger.warn("Entity clash detected at ID: " + entity.getId());
                int nextFreeId = getNextFreeId();
                logger.warn("New id for " + entity.getName() + ": " + nextFreeId);
                entity.setId(nextFreeId);
            } else {
                logger.warn("Skipping duplicate registration of [" + entity.getId() + "] " + entity.getName() + ".");
                return;
            }
        }
        entityList.add(entity);
        logger.info("Successfully registered [" + entity.getId() + "] " + entity.getName() + ".");
    }

    public List<T> getEntityList() {
        return new ArrayList<>(entityList);
    }

    public T getEntityById(int id) {
        return entityList.stream()
            .filter(entity -> entity.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public T getEntityByRarity(Rarity rarity) {
        if (classInstance.equals(ItemEntity.class)) {
            return entityList.stream()
                .filter(entity -> ((ItemEntity) entity).getRarity().equals(rarity))
                .findFirst()
                .orElse(null);
        }

        return null;
    }

    public List<SelectOption> getOptionList(int defaultIndex) {
        return entityList.stream()
            .filter(entity -> entity.getId() > 0)
            .map(entity ->
                SelectOption
                    .of(entity.getName(), "place-" + entity.getId())
                    .withDefault(entity.getId().equals(defaultIndex))
            )
            .toList();
    }
}
