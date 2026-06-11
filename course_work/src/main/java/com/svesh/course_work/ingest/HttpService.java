package com.svesh.course_work.ingest;

import com.svesh.course_work.api.ApiRequest;
import okhttp3.*;

import java.io.IOException;

public class HttpService {
    private static final OkHttpClient CLIENT = new OkHttpClient();

    private String buildUrl(ApiRequest request) {
        HttpUrl.Builder builder = HttpUrl.get(request.api().getApiUrl()).newBuilder();
        request.queryParams().forEach(builder::addQueryParameter);
        return builder.build().toString();
    }

    public String fetch(ApiRequest request) throws IOException {
        Request httpRequest = new Request.Builder()
                .url(buildUrl(request))
                .header("User-Agent", "DataAggregator/1.0")
                .header("Accept", "application/json")
                .build();

        try (Response response = CLIENT.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error: " + response.code());
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Empty response body");
            }
            return body.string();
        }
    }
}
