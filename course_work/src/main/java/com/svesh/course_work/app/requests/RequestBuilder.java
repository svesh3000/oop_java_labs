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

    public List<ApiRequest> build(List<String> apiNames,
                                  Map<String, Map<String, String>> paramsByApi) {
        List<ApiRequest> requests = new ArrayList<>(apiNames.size());
        for (String name : apiNames) {
            ApiDefinition api = registry.get(name);
            if (api == null) {
                throw new IllegalArgumentException("Unknown API: " + name);
            }
            Map<String, String> params = paramsByApi.get(name);
            if (params == null) {
                params = api.getDefaultQueryParams();
            }
            requests.add(new ApiRequest(api, params));
        }
        return requests;
    }
}
