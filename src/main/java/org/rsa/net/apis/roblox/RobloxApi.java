package org.rsa.net.apis.roblox;

import com.google.common.util.concurrent.RateLimiter;
import org.rsa.net.apis.roblox.models.thumbnails.ThumbnailModel;
import org.rsa.net.apis.roblox.models.users.MultiGetUserByNameModel;
import org.rsa.net.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobloxApi {
    private final RateLimiter rateLimiter;
    private final HttpClient httpClient;
    private static final String USERS_BASE_URL = "https://users.roblox.com";
    private static final String THUMBNAILS_BASE_URL = "https://thumbnails.roblox.com";

    public RobloxApi(RateLimiter rateLimiter, HttpClient httpClient) {
        this.rateLimiter = rateLimiter;
        this.httpClient = httpClient;
    }

    public MultiGetUserByNameModel multiGetUserByName(List<String> usernames, boolean excludeBannedUsers) throws IOException {
        String GET_USERS_ENDPOINT = USERS_BASE_URL + "/v1/usernames/users";
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("usernames", usernames);
        requestBodyMap.put("excludeBannedUsers", excludeBannedUsers);

        rateLimiter.acquire();
        return httpClient.post(GET_USERS_ENDPOINT, requestBodyMap, MultiGetUserByNameModel.class);
    }

    public ThumbnailModel getAvatarHeadshot(List<Long> userIds, String thumbnailSize, String format, Boolean isCircular) throws IOException {
        String GET_AVATAR_HEADSHOT_ENDPOINT = THUMBNAILS_BASE_URL + "/v1/users/avatar-headshot";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userIds", userIds);
        queryParams.put("size", thumbnailSize);
        queryParams.put("format", format);
        queryParams.put("isCircular", isCircular.toString());

        rateLimiter.acquire();
        return httpClient.get(httpClient.buildUrlWithParams(GET_AVATAR_HEADSHOT_ENDPOINT, queryParams), ThumbnailModel.class);
    }
}