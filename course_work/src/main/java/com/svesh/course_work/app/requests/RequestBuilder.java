package com.svesh.course_work.app.requests;

import com.svesh.course_work.api.ApiDefinition;
import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.api.ApiRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestBuilder {
    private final ApiRegistry registry;

    public RequestBuilder(ApiRegistry registry) {
        this.registry = registry;
    }

    public List<ApiRequest> buildDefault(List<String> apiNames) {
        List<ApiRequest> requests = new ArrayList<>();
        for (String name : apiNames) {
            ApiDefinition api = registry.get(name);
            if (api == null) {
                throw new IllegalArgumentException("Unknown API: " + name);
            }
            requests.add(new ApiRequest(api, api.getDefaultQueryParams()));
        }
        return requests;
    }

    public ApiRequest build(String apiName, Map<String, String> params) {
        ApiDefinition api = registry.get(apiName);
        if (api == null) {
            throw new IllegalArgumentException("Unknown API: " + apiName);
        }
        return new ApiRequest(api, params);
    }
}
