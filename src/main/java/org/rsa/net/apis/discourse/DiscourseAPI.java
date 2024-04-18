package org.rsa.net.apis.discourse;

import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import org.rsa.net.apis.discourse.domain.Category;
import org.rsa.net.apis.discourse.domain.Topic;
import org.rsa.net.apis.discourse.models.CategoryModel;
import org.rsa.net.apis.discourse.models.CategoryTopicsModel;
import org.rsa.net.apis.discourse.models.SiteBasicInfoModel;
import org.rsa.net.apis.discourse.models.TopicModel;
import org.rsa.net.HttpClient;
import org.rsa.net.apis.discourse.transformers.CategoryTransformer;
import org.rsa.net.apis.discourse.transformers.TopicTransformer;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class DiscourseAPI {
    private static final RateLimiter rateLimiter = RateLimiter.create(0.4); // 1 API hit every 3 seconds
    private static final @Getter String BASE_URL = "https://devforum.roblox.com";
    private static final String CATEGORIES_URL = BASE_URL + "/site.json";
    private static final String CATEGORY_URL_FORMAT = BASE_URL + "/c/%s.json";
    private static final String TOPICS_URL_FORMAT = BASE_URL + "/t/%s.json";
    private static final String SITE_BASIC_INFO_URL = BASE_URL + "/site/basic-info.json";
    private static Map<String, Category> latestCategoryInformation;

    public static SiteBasicInfoModel fetchSiteBasicInfo() throws IOException {
        rateLimiter.acquire();
        return HttpClient.get(SITE_BASIC_INFO_URL, SiteBasicInfoModel.class);
    }

    public static Map<String, Category> fetchAllCategoryInformation() throws IOException {
        rateLimiter.acquire();
        CategoryModel responseObj = HttpClient.get(CATEGORIES_URL, CategoryModel.class);
        Map<String, Category> categoryDetails = CategoryTransformer.fromResponse(responseObj);
        return categoryDetails;
    }

    public static Optional<String> fetchLatestNonPinnedTopicId(String categoryId) throws IOException {
        String url = String.format(CATEGORY_URL_FORMAT, categoryId);

        rateLimiter.acquire();
        CategoryTopicsModel responseObj = HttpClient.get(url, CategoryTopicsModel.class);
        // Grabs the first non-pinned item in the list, if it exists
        return responseObj.topic_list().topics().stream()
                .filter(topic -> !topic.pinned())
                .findFirst()
                .map(CategoryTopicsModel.TopicList.Topic::id);
    }

    public static Topic fetchLatestPostInCategory(String categoryId) throws IOException, IllegalArgumentException {
        Optional<String> topicIdOptional = fetchLatestNonPinnedTopicId(categoryId);
        String topicId = topicIdOptional.orElseThrow(() -> new IllegalArgumentException("No non-pinned topic found for category ID: " + categoryId));
        String url = String.format(TOPICS_URL_FORMAT, topicId);

        rateLimiter.acquire();
        TopicModel topicModel = HttpClient.get(url, TopicModel.class);
        return TopicTransformer.fromResponse(topicModel);
    }

    public static Map<String, Category> getLatestCategoryInformation() throws IOException {
        if (latestCategoryInformation != null) {
            return latestCategoryInformation;
        } else {
            return fetchAllCategoryInformation();
        }
    }
}
