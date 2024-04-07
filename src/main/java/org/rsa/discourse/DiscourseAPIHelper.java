package org.rsa.discourse;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.rsa.discourse.models.CategoryDetailsModel;
import org.rsa.discourse.models.TopicDetailsModel;
import org.rsa.exception.ApiException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DiscourseAPIHelper {
    private static class CategoryTopicsResponse {
        TopicList topic_list;

        static class TopicList {
            List<Topic> topics;

            static class Topic {
                String id;
                boolean pinned;
            }
        }
    }

    private static Map<String, CategoryDetailsModel> latestCategoriesData;
    private static final RateLimiter rateLimiter = RateLimiter.create(0.1);
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final String baseUrl = "https://devforum.roblox.com/";

    public static Map<String, CategoryDetailsModel> getLatestCategoriesData() {
        if (latestCategoriesData == null) {
            try {
                latestCategoriesData = fetchAllCategoryInformation();
            } catch (IOException | ApiException e) {
                System.err.println(e.getMessage());
            }
        }

        return latestCategoriesData;
    }

    public static Map<String, CategoryDetailsModel> fetchAllCategoryInformation() throws IOException, ApiException {
        String url = baseUrl + "/site.json";
        Request request = new Request.Builder().url(url).build();

        rateLimiter.acquire();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("Unexpected code " + response.code(), response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new ApiException("Response body is null for URL: " + url, response.code());
            }

            CategoryDetailsModel.CategoriesResponse responseObj = gson.fromJson(responseBody.string(), CategoryDetailsModel.CategoriesResponse.class);
            Map<String, CategoryDetailsModel> latestCategoriesData = CategoryDetailsModel.fromCategoriesResponse(responseObj);
            return latestCategoriesData;
        }
    }

    public static String fetchLatestNonPinnedTopicId(String categoryId) throws IOException, ApiException {
        String url = baseUrl + "/c/" + categoryId + ".json";
        Request request = new Request.Builder().url(url).build();

        rateLimiter.acquire();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("Unexpected code " + response.code(), response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new ApiException("Response body is null for URL: " + url, response.code());
            }

            CategoryTopicsResponse responseObj = gson.fromJson(responseBody.string(), CategoryTopicsResponse.class);
            for (CategoryTopicsResponse.TopicList.Topic topic : responseObj.topic_list.topics) {
                if (!topic.pinned) {
                    return topic.id;
                }
            }
        }

        return null;
    }

    public static TopicDetailsModel fetchLatestPostInCategory(String categoryId) throws IOException, ApiException {
        String topicId = fetchLatestNonPinnedTopicId(categoryId);

        String url = baseUrl + "/t/" + topicId + ".json";
        Request request = new Request.Builder().url(url).build();

        rateLimiter.acquire();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ApiException("Unexpected code " + response.code(), response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new ApiException("Response body is null for URL: " + url, response.code());
            }

            TopicDetailsModel.TopicResponse topicResponse = gson.fromJson(responseBody.string(), TopicDetailsModel.TopicResponse.class);
            return TopicDetailsModel.fromTopicResponse(topicResponse);
        }
    }
}
