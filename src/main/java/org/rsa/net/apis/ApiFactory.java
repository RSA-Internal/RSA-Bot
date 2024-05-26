package org.rsa.net.apis;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import org.rsa.net.HttpClient;
import org.rsa.net.apis.discourse.DiscourseApi;
import org.rsa.net.apis.discourse.transformers.CategoryTransformer;
import org.rsa.net.apis.discourse.transformers.TopicTransformer;
import org.rsa.net.apis.roblox.RobloxApi;
import org.rsa.net.apis.wandbox.WandboxApi;

public class ApiFactory {
    private static DiscourseApi discourseAPI;
    private static WandboxApi wandboxAPI;
    private static RobloxApi robloxAPI;

    public static DiscourseApi getDiscourseApi() {
        if (discourseAPI == null) {
            RateLimiter rateLimiter = RateLimiter.create(0.2); // 1 request every 5 seconds
            OkHttpClient okHttpClient = new OkHttpClient();
            Gson gson = new GsonBuilder().create();
            HttpClient httpClient = new HttpClient(okHttpClient, gson);
            TopicTransformer topicTransformer = new TopicTransformer();
            CategoryTransformer categoryTransformer = new CategoryTransformer();
            discourseAPI = new DiscourseApi(rateLimiter, httpClient, topicTransformer, categoryTransformer, "https://devforum.roblox.com");
        }
        return discourseAPI;
    }

    public static WandboxApi getWandboxApi() {
        if (wandboxAPI == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            Gson gson = new GsonBuilder().create();
            HttpClient httpClient = new HttpClient(okHttpClient, gson);
            wandboxAPI = new WandboxApi(httpClient, "https://wandbox.org");
        }
        return wandboxAPI;
    }

    public static RobloxApi getRobloxApi() {
        if (robloxAPI == null) {
            RateLimiter rateLimiter = RateLimiter.create(1); // 1 request every second
            OkHttpClient okHttpClient = new OkHttpClient();
            Gson gson = new GsonBuilder().create();
            HttpClient httpClient = new HttpClient(okHttpClient, gson);
            robloxAPI = new RobloxApi(rateLimiter, httpClient);
        }
        return robloxAPI;
    }
}