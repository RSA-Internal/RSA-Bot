package org.rsa.entity;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.rsa.adventure.model.Rarity;
import org.rsa.entity.adventure.ItemEntity;

import java.util.ArrayList;
import java.util.List;

public class EntityManager<T extends BaseEntity> {

    private final Class<T> classInstance;
    private final List<T> entityList = new ArrayList<>();

    public EntityManager(Class<T> clazz) {
        classInstance = clazz;
    }

    public void addEntity(T entity) throws Exception {
        T existingEntity = getEntityById(entity.getId());
        if (existingEntity != null) {
            if (!existingEntity.equals(entity)) {
                throw new Exception("[entity clash] Failed to add '[" + entity.getId() + "] " + entity.getName()
                    + "' because '[" + existingEntity.getId() + "] " + existingEntity.getName()
                    + "' already exists at ID: " + entity.getId() + "!");
            }
        }
        entityList.add(entity);
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
