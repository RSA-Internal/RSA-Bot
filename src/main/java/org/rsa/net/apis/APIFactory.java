package org.rsa.net.apis;

import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import org.rsa.net.HttpClient;
import org.rsa.net.apis.discourse.DiscourseAPI;
import org.rsa.net.apis.discourse.transformers.CategoryTransformer;
import org.rsa.net.apis.discourse.transformers.TopicTransformer;
import org.rsa.net.apis.roblox.RobloxAPI;
import org.rsa.net.apis.wandbox.WandboxAPI;

public class APIFactory {
    private static DiscourseAPI discourseAPI;
    private static WandboxAPI wandboxAPI;
    private static RobloxAPI robloxAPI;

    public static DiscourseAPI getDiscourseAPI() {
        if (discourseAPI == null) {
            RateLimiter rateLimiter = RateLimiter.create(0.2); // 1 request every 5 seconds
            OkHttpClient okHttpClient = new OkHttpClient();
            Gson gson = new GsonBuilder().create();
            HttpClient httpClient = new HttpClient(okHttpClient, gson);
            TopicTransformer topicTransformer = new TopicTransformer();
            CategoryTransformer categoryTransformer = new CategoryTransformer();
            discourseAPI = new DiscourseAPI(rateLimiter, httpClient, topicTransformer, categoryTransformer, "https://devforum.roblox.com");
        }
        return discourseAPI;
    }

    public static WandboxAPI getWandboxAPI() {
        if (wandboxAPI == null) {
            OkHttpClient okHttpClient = new OkHttpClient();
            Gson gson = new GsonBuilder().create();
            HttpClient httpClient = new HttpClient(okHttpClient, gson);
            wandboxAPI = new WandboxAPI(httpClient, "https://wandbox.org");
        }
        return wandboxAPI;
    }

    public static RobloxAPI getRobloxAPI() {
        if (robloxAPI == null) {
            RateLimiter rateLimiter = RateLimiter.create(1); // 1 request every second
            OkHttpClient okHttpClient = new OkHttpClient();
            Gson gson = new GsonBuilder().create();
            HttpClient httpClient = new HttpClient(okHttpClient, gson);
            robloxAPI = new RobloxAPI(rateLimiter, httpClient);
        }
        return robloxAPI;
    }
}