package org.rsa.task.tasks;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.rsa.net.apis.discourse.DiscourseAPI;
import org.rsa.net.apis.discourse.domain.Category;
import org.rsa.net.apis.discourse.domain.Topic;
import org.rsa.net.apis.roblox.RobloxAPI;
import org.rsa.net.apis.roblox.models.users.MultiGetUserByNameModel;
import org.rsa.task.TaskObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DevforumUpdateTask extends TaskObject {
    private static final String TASK_NAME = "DevforumUpdateTask";
    private static final long UPDATE_INTERVAL = 5;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
    private static final String AUTHOR_FORMAT = "Developer Forum | Roblox | %s";
    private static final String FOOTER_FORMAT = "Posted by %s";
    private static final String TRUNCATE_FORMAT_STRING = "\n\n[(Read more...)](%s)";
    private static String THUMBNAIL_URL = "https://devforum-uploads.s3.dualstack.us-east-2.amazonaws.com/uploads/original/5X/c/3/2/5/c325b8f46fd0b3c5b418b005b66c7af661539d44.png";
    private static final int MAX_DESCRIPTION_LENGTH_CHAR = 500;

    public DevforumUpdateTask(JDA jda, ScheduledExecutorService scheduler) {
        super(TASK_NAME, jda, scheduler);

        try {
            THUMBNAIL_URL = DiscourseAPI.fetchSiteBasicInfo().logo_url(); // Try and grab the latest thumbnail from forum
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }

    protected void execute() {
        try {
            Map<String, Category> categories = DiscourseAPI.fetchAllCategoryInformation();
            categories.forEach((categoryId, categoryDetails) -> {
                try {
                    Topic topicResponse = DiscourseAPI.fetchLatestPostInCategory(categoryId);
                    MessageEmbed messageEmbed = createEmbed(topicResponse, categoryDetails);
                    jda.getTextChannelById("1187145914343764129").sendMessageEmbeds(messageEmbed).queue();
                } catch(IOException e) {
                    System.err.println(e.getMessage());
                }
            });
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void startTask() {
        scheduledTask = scheduler.scheduleAtFixedRate(this::execute, 0, UPDATE_INTERVAL, TIME_UNIT);
    }

    private MessageEmbed createEmbed(Topic topicDetails, Category postCategoryDetails) {
        String baseUrl = DiscourseAPI.getBASE_URL();
        String topicUrl = baseUrl + "/t/" + topicDetails.id();
        String categoryUrl = baseUrl + "/c/" + postCategoryDetails.id();
        String description = trimDescription(topicDetails, topicUrl);

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(String.format(AUTHOR_FORMAT, postCategoryDetails.name()), categoryUrl)
                .setTitle(topicDetails.title())
                .setDescription(description)
                .setUrl(topicUrl)
                .setThumbnail(THUMBNAIL_URL)
                .setColor(postCategoryDetails.categoryColor())
                .setTimestamp(topicDetails.createdAt());

        try {
            // Grab profile image URL from thumbnails endpoint by getting ID from username
            MultiGetUserByNameModel.User user = RobloxAPI.Users.multiGetUserByName(List.of(topicDetails.author()), true).data().get(0);
            String imgUrl = RobloxAPI.Thumbnails.getAvatarHeadshot(List.of(user.id()), "50x50", "Png", false).data().get(0).imageUrl();
            embedBuilder.setFooter(String.format(FOOTER_FORMAT, topicDetails.author()), imgUrl);
        } catch(IOException | IndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
            embedBuilder.setFooter(String.format(FOOTER_FORMAT, topicDetails.author()));
        }

        if (topicDetails.imageUrl() != null) {
            embedBuilder.setImage(topicDetails.imageUrl());
        }

        return embedBuilder.build();
    }

    private static @NotNull String trimDescription(Topic topicDetails, String topicUrl) {
        String description = topicDetails.parsedContent();

        // Truncate description after a space, if possible
        if (description.length() > MAX_DESCRIPTION_LENGTH_CHAR) {
            int end = MAX_DESCRIPTION_LENGTH_CHAR - 3;
            int lastSpace = description.lastIndexOf(' ', end);
            if (lastSpace < 0) {
                description = description.substring(0, end);
            } else {
                description = description.substring(0, lastSpace);
            }

            description += String.format(TRUNCATE_FORMAT_STRING, topicUrl);
        }
        return description;
    }
}
