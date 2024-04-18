package org.rsa.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpClient {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new GsonBuilder().create();

    public static <T> T get(String url, Type typeOfT) throws IOException {
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            if (response.body() == null) throw new IOException("Method was GET but response body is NULL");

            return gson.fromJson(response.body().string(), typeOfT);
        }
    }

    public static <T> T post(String url, Object payload, Type typeOfT) throws IOException {
        RequestBody body = RequestBody.create(
                gson.toJson(payload),
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            if (response.body() == null) throw new IOException("Method was POST but successful response returned NULL body");
            ResponseBody responseBody = response.body();
            String jsonString = responseBody.string();

            if (jsonString.equals("{}")) {
                return null;
            }

            return gson.fromJson(jsonString, typeOfT);
        }
    }

    public static String buildUrlWithParams(String url, Map<String, Object> params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof List) {
                for (Object val : (List<?>) entry.getValue()) {
                    urlBuilder.addQueryParameter(entry.getKey(), val.toString());
                }
            } else {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
            }
        }
        return urlBuilder.build().toString();
    }
}
