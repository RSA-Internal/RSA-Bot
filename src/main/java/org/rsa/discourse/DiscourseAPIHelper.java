package org.rsa.discourse;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.rsa.discourse.models.CategoryDetailsModel;
import org.rsa.discourse.models.TopicDetailsModel;

import java.io.IOException;
import java.util.HashMap;
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

    private final OkHttpClient client;
    private final Gson gson;
    private final String baseUrl;

    public DiscourseAPIHelper(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public Map<String, CategoryDetailsModel> getAllCategoryInformation() throws IOException {
        String url = baseUrl + "/site.json";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            CategoryDetailsModel.CategoriesResponse responseObj = gson.fromJson(response.body().string(), CategoryDetailsModel.CategoriesResponse.class);

            return  CategoryDetailsModel.fromCategoriesResponse(responseObj);
        }
    }

    public String getLatestNonPinnedTopicId(String categoryId) throws IOException {
        String url = baseUrl + "/c/" + categoryId + ".json";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            CategoryTopicsResponse responseObj = gson.fromJson(response.body().string(), CategoryTopicsResponse.class);

            for (CategoryTopicsResponse.TopicList.Topic topic : responseObj.topic_list.topics) {
                if (!topic.pinned) {
                    return topic.id;
                }
            }
        }

        return null;
    }

    public TopicDetailsModel getLatestPostInCategory(String categoryId) throws IOException {
        String topicId = getLatestNonPinnedTopicId(categoryId);
        if (topicId == null) {
            throw new IOException("No non-pinned topics found in category " + categoryId);
        }

        String url = baseUrl + "/t/" + topicId + ".json";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            TopicDetailsModel.TopicResponse topicResponse = gson.fromJson(response.body().string(), TopicDetailsModel.TopicResponse.class);
            return TopicDetailsModel.fromTopicResponse(topicResponse);
        }
    }
}
