package org.rsa.net.apis.discourse;

import com.google.common.util.concurrent.RateLimiter;
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

public class DiscourseApi {
    private final RateLimiter rateLimiter;
    private final HttpClient httpClient;
    private Map<String, Category> latestCategoryInformation;
    private final TopicTransformer topicTransformer;
    private final CategoryTransformer categoryTransformer;
    private final String BASE_URL;
    private final String CATEGORIES_URL;
    private final String CATEGORY_URL_FORMAT;
    private final String TOPICS_URL_FORMAT;
    private final String SITE_BASIC_INFO_URL;

    public DiscourseApi(RateLimiter rateLimiter, HttpClient httpClient, TopicTransformer topicTransformer, CategoryTransformer categoryTransformer, String baseUrl) {
        this.rateLimiter = rateLimiter;
        this.httpClient = httpClient;
        this.topicTransformer = topicTransformer;
        this.categoryTransformer = categoryTransformer;
        this.BASE_URL = baseUrl;
        this.CATEGORIES_URL = baseUrl + "/site.json";
        this.CATEGORY_URL_FORMAT = baseUrl + "/c/%s.json";
        this.TOPICS_URL_FORMAT = baseUrl + "/t/%s.json";
        this.SITE_BASIC_INFO_URL = baseUrl + "/site/basic-info.json";
    }

    public SiteBasicInfoModel fetchSiteBasicInfo() throws IOException {
        rateLimiter.acquire();
        return httpClient.get(SITE_BASIC_INFO_URL, SiteBasicInfoModel.class);
    }

    public Map<String, Category> fetchAllCategoryInformation() throws IOException {
        rateLimiter.acquire();
        CategoryModel responseObj = httpClient.get(CATEGORIES_URL, CategoryModel.class);
        Map<String, Category> categoryDetails = categoryTransformer.fromResponse(responseObj);
        latestCategoryInformation = categoryDetails;
        return categoryDetails;
    }

    public Optional<String> fetchLatestNonPinnedTopicId(String categoryId) throws IOException {
        String url = String.format(CATEGORY_URL_FORMAT, categoryId);

        rateLimiter.acquire();
        CategoryTopicsModel responseObj = httpClient.get(url, CategoryTopicsModel.class);
        // Grabs the first non-pinned item in the list, if it exists
        return responseObj.topic_list().topics().stream()
                .filter(topic -> !topic.pinned())
                .findFirst()
                .map(CategoryTopicsModel.TopicList.Topic::id);
    }

    public Topic fetchLatestPostInCategory(String categoryId) throws IOException, IllegalArgumentException {
        Optional<String> topicIdOptional = fetchLatestNonPinnedTopicId(categoryId);
        String topicId = topicIdOptional.orElseThrow(() -> new IllegalArgumentException("No non-pinned topic found for category ID: " + categoryId));
        String url = String.format(TOPICS_URL_FORMAT, topicId);

        rateLimiter.acquire();
        TopicModel topicModel = httpClient.get(url, TopicModel.class);
        return topicTransformer.fromResponse(topicModel);
    }

    public Map<String, Category> getLatestCategoryInformation() throws IOException {
        if (latestCategoryInformation != null) {
            return latestCategoryInformation;
        } else {
            return fetchAllCategoryInformation();
        }
    }

    public String getBaseUrl() {
        return BASE_URL;
    }
}
