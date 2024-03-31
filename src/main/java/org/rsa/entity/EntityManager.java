package org.rsa.entity;

import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class EntityManager<T extends BaseEntity> {

    private final Logger logger = LoggerFactory.getLogger(EntityManager.class);
    private final List<T> entityList = new ArrayList<>();

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
        return getEntityList("");
    }

    public List<T> getEntityList(String filter) {
        return new ArrayList<>(entityList).stream().filter(entity -> entity.getName().toLowerCase().contains(filter.toLowerCase())).toList();
    }

    public Stream<T> getEntityStream() {
        return getEntityList().stream();
    }

    public T getEntityById(int id) {
        return entityList.stream()
            .filter(entity -> entity.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public List<SelectOption> getOptionList(int defaultIndex, int page) {
        return getOptionList(defaultIndex, page, "");
    }

    public List<SelectOption> getOptionList(int defaultIndex, int page, String filter) {
        AtomicInteger index = new AtomicInteger();
        Stream<T> paginatedEntities = getPaginatedEntities(page, filter, Comparator.comparing(BaseEntity::getName));

        return paginatedEntities.map(entity -> {
                SelectOption generator = SelectOption
                    .of(entity.getName(), "entity-" + entity.getId() + "-" + index)
                    .withDefault(index.get() == defaultIndex);
                index.getAndIncrement();
                return generator;
            })
            .toList();
    }

    public Stream<T> getPaginatedEntities(int page, String filter) {
        return getPaginatedEntities(page, filter, Comparator.comparing(BaseEntity::getName));
    }

    public Stream<T> getPaginatedEntities(int page, Comparator<? super T> comparator) {
        return getPaginatedEntities(page, "", comparator);
    }

    public Stream<T> getPaginatedEntities(int page, String filter, Comparator<? super T> comparator) {
        Stream<T> streamingEntities = entityList.stream()
            .filter(entity -> entity.getId() > 0);

        if (filter != null && !filter.isEmpty()) {
            streamingEntities = streamingEntities.filter(entity -> entity.getName().toLowerCase().contains(filter.toLowerCase()));
            // TODO: Filter recipes inputs and outputs
            // TODO: Filter loot tables
        }

        if (null != comparator) {
            streamingEntities = streamingEntities.sorted(comparator);
        }

        return streamingEntities
            .skip(page * 25L)
            .limit(25);
    }

    public String getLetterRangeForPage(int page) {
        return getLetterRangeForPage(page, "");
    }

    public String getLetterRangeForPage(int page, String filter) {
        Stream<T> paginatedEntities = getPaginatedEntities(page, filter, Comparator.comparing(BaseEntity::getName));
        List<String> names = paginatedEntities.map(BaseEntity::getName).toList();
        String firstName = names.get(0);
        String lastName = names.get(names.size() - 1);

        return firstName.charAt(0) + "-" + lastName.charAt(0);
    }

    public int getPageCount() {
        return getPageCount("");
    }

    public int getPageCount(String filter) {
        List<T> filteredEntities = getEntityList(filter);

        int remaining = filteredEntities.size() % 25;
        int pageCount = filteredEntities.size() / 25;
        if (remaining > 0) {
            pageCount += 1;
        }
        return pageCount;
    }
}
