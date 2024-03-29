package org.rsa.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.rsa.register.adventure.EntityManagerRegister;
import org.rsa.manager.adventure.IndexManager;
import org.rsa.model.adventure.entity.Activity;
import org.rsa.model.adventure.response.ActivityPerformResponse;
import org.rsa.entity.BaseEntity;
import org.rsa.entity.EntityManager;
import org.rsa.entity.adventure.ActivityEntity;
import org.rsa.entity.adventure.ItemEntity;
import org.rsa.entity.adventure.SkillEntity;
import org.rsa.entity.adventure.ZoneEntity;
import org.rsa.logic.data.models.UserAdventureProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.rsa.translator.AdventureTravelTranslator.getTravelStringBuilder;
import static org.rsa.util.StringUtil.capitalizeFirst;

public class EmbedBuilderUtil {

    private static final Logger logger = LoggerFactory.getLogger(EmbedBuilderUtil.class);

    public static EmbedBuilder getEmbedBuilderTemplate(Guild guild, Member requester, String title) {
        return getEmbedBuilderTemplate(guild, requester, title, null);
    }

    public static EmbedBuilder getEmbedBuilderTemplate(Guild guild, Member requester, String title, Member profileOwner) {
        return new EmbedBuilder()
            .setTitle(title)
            .setAuthor(requester.getEffectiveName())
            .setColor(HelperUtil.getColorFromProfile(guild, requester))
            .setThumbnail(profileOwner != null ? profileOwner.getEffectiveAvatarUrl() : requester.getEffectiveAvatarUrl())
            .setFooter(requester.getId());
    }

    public static EmbedBuilder getActivitySummaryEmbedBuilder(Guild guild, Member requester, UserAdventureProfile adventureProfile, String title, ActivityPerformResponse performResponse) {
        // Display results.
        EmbedBuilder builder = getEmbedBuilderTemplate(guild, requester, title);

        if (!performResponse.getMessages().isEmpty() && !title.equals("Travel Summary")) {
            String messageList = performResponse.getMessages().stream().map(msg -> "- " + msg).collect(Collectors.joining("\n"));

            builder.addField("Messages", messageList, true);
        } else {
            Map<ItemEntity, Integer> itemsReceived = performResponse.getItemsReceived();
            String itemReceivedDisplay = itemsReceived.keySet().stream()
                .map(item -> "- " + itemsReceived.get(item) + " " + item.getName())
                .collect(Collectors.joining("\n"));
            if (itemReceivedDisplay.isEmpty()) {
                itemReceivedDisplay = "- None";
            }
            builder.addField("Items Received", itemReceivedDisplay, false);

            Map<SkillEntity, Integer> experienceGained = performResponse.getExperienceGained();
            String experienceGainedDisplay = experienceGained.keySet().stream()
                .map(skill -> "- " + skill.getName() + " " + experienceGained.get(skill) + " xp")
                .collect(Collectors.joining("\n"));
            if (experienceGainedDisplay.isEmpty()) {
                experienceGainedDisplay = "- None";
            }
            builder.addField("Experience Gained", experienceGainedDisplay, false);

            List<SkillEntity> skillsLeveled = performResponse.getSkillsLeveledUp();
            if (!skillsLeveled.isEmpty()) {
                StringBuilder skillBuilder = new StringBuilder();
                Set<SkillEntity> uniqueLevels = new HashSet<>(skillsLeveled);
                for (SkillEntity skill : uniqueLevels) {
                    int currentLevel = adventureProfile.getSkillSetLevel().get(skill.getId());
                    int timesLeveled = (int) skillsLeveled.stream().filter(leveledSkill -> leveledSkill.getId().equals(skill.getId())).count();
                    int previousLevel = currentLevel - timesLeveled;
                    skillBuilder.append("- ");
                    skillBuilder.append(skill.getName());
                    skillBuilder.append(": ");
                    skillBuilder.append(previousLevel);
                    skillBuilder.append(" -> ");
                    skillBuilder.append(currentLevel);
                }
                builder.addField("Skills Leveled Up", skillBuilder.toString(), false);
            }

            Set<ZoneEntity> unlockedZones = performResponse.getUnlockedZones();
            if (!unlockedZones.isEmpty()) {
                String unlockedZoneDisplay = unlockedZones.stream()
                    .map(zone -> "- " + zone.getName())
                    .collect(Collectors.joining("\n"));
                builder.addField("Zones Unlocked", unlockedZoneDisplay, false);
            }
        }

        return builder;
    }

    public static EmbedBuilder getTravelEmbedBuilder(Guild guild, Member requester, ZoneEntity zone) {
        long startTime = System.currentTimeMillis();
        EmbedBuilder builder = getEmbedBuilderTemplate(guild, requester, "Location " + zone.getName());

        for (ActivityEntity activity : zone.getActivities()) {
            if (!activity.getId().equals(Activity.LEAVE.getId())) {
                List<SkillEntity> requiredSkills = activity.getRequiredSkillSet();
                List<ItemEntity> requiredItemsList = activity.getRequiredItems();
                Integer experienceBound = activity.getExperienceGainBound();

                String requiredLevels = requiredSkills.stream()
                    .filter(skill -> skill.getLevel() > 0)
                    .map(skill -> " - " + skill.getName() + ": " + skill.getLevel())
                    .collect(Collectors.joining("\n"));
                String requiredItems = requiredItemsList.stream()
                    .map(item -> " - " + item.getName())
                    .collect(Collectors.joining("\n"));
                String possibleItems = ActivityEntity.getPossibleItemsAsString(activity, false, false);

                StringBuilder requiredDisplay = getTravelStringBuilder(requiredLevels, requiredItems, experienceBound, possibleItems);

                builder.addField(activity.getName(), requiredDisplay.toString(), true);
                builder.setFooter(requester.getId());
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time of 'getTravelEmbedBuilder': " + (endTime-startTime) + "ms");
        return builder;
    }

    public static EmbedBuilder getIndexEmbedBuilder(Member requester) {
        String type = IndexManager.getUserTypeSelection(requester.getId());
        int entityId = IndexManager.getUserSelectedEntityId(requester.getId());
        return getIndexEmbedBuilder(requester, type, entityId);
    }

    public static EmbedBuilder getIndexEmbedBuilder(Member requester, String entityType, int selectedEntityId) {
        logger.info("getIndexEmbed - requester: {} | type: {} | entity: {}", requester.getId(), entityType, selectedEntityId);
        EntityManager<?> entityManager = EntityManagerRegister.getEntityManagerFromType(entityType.toLowerCase());

        if (selectedEntityId == -1) {
            BaseEntity firstEntity = entityManager.getPaginatedEntities(IndexManager.getUserPage(requester.getId()), Comparator.comparing(BaseEntity::getName)).findFirst().orElse(null);
            if (firstEntity != null) {
                selectedEntityId = firstEntity.getId();
                IndexManager.setUserSelectedEntityId(requester.getId(), selectedEntityId);
            }
        }

        BaseEntity baseEntity = entityManager.getEntityById(selectedEntityId);
        logger.info("getIndexEmbed - BaseEntity: {}", baseEntity);

        return new EmbedBuilder()
            .setTitle("Index Viewer")
            .setAuthor(requester.getEffectiveName())
            .setColor(HelperUtil.getRandomColor())
            .setThumbnail("https://cdn4.iconfinder.com/data/icons/learning-31/64/dictionary_book_lexicon_work_book_thesaurus-512.png")
            .addField("Viewing: " + capitalizeFirst(entityType) + " - " + baseEntity.getName(), baseEntity.getAsDetails(), true);
    }
}
