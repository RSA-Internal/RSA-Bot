package org.rsa.logic.data.managers;

import org.rsa.aws.RequestsManager;
import org.rsa.logic.data.models.DevforumUpdates;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class DevforumUpdatesManager {
    private static final String TABLE_NAME = "devforum_updates_data";
    private final static RequestsManager<DevforumUpdates> requestsManager = new RequestsManager<>(TABLE_NAME, DevforumUpdates.class);
    private final ReentrantLock lock = new ReentrantLock();

    public static DevforumUpdates fetch(String guildId)
    {
        QueryConditional query = QueryConditional
                .keyEqualTo(Key.builder()
                        .partitionValue(guildId)
                        .build());
        Optional<Page<DevforumUpdates>> optionalPage = requestsManager.fetchSingleItem(query);

        if (optionalPage.isEmpty() || optionalPage.get().items().isEmpty()) return new DevforumUpdates(guildId);
        return optionalPage.get().items().get(0);
    }

    public void updateLastPost(String guildId, int categoryId, int postId) {
        lock.lock();
        try {
            DevforumUpdates devforumUpdates = fetch(guildId);
            Map<Integer, Integer> lastPosts = devforumUpdates.getLast_posts();
            lastPosts.put(categoryId, postId);
            requestsManager.enqueueItemUpdate(devforumUpdates);
        } catch (Exception e) {
            System.err.println("Failed to update last post: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void updateSubscriptionStatus(String guildId, int categoryId) {
        lock.lock();
        try {
            DevforumUpdates devforumUpdates = fetch(guildId);
            List<Integer> enabledTopics = devforumUpdates.getEnabled_topics();
            if (!enabledTopics.contains(categoryId)) {
                    enabledTopics.add(categoryId);
            } else {
                enabledTopics.remove((Integer) categoryId);
            }
            requestsManager.enqueueItemUpdate(devforumUpdates);
        } catch (Exception e) {
            System.err.println("Failed to set subscription status: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void changeChannelId(String guildId, String newChannelId) {
        lock.lock();
        try {
            DevforumUpdates devforumUpdates = fetch(guildId);
            devforumUpdates.setChannelid(newChannelId);
            requestsManager.enqueueItemUpdate(devforumUpdates);
        } catch (Exception e) {
            System.err.println("Failed to change channel ID: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
