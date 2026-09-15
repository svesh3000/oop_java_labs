package com.svesh.course_work.api;

import com.svesh.course_work.api.impl.JokeApi;
import com.svesh.course_work.api.impl.OpenMeteoApi;
import com.svesh.course_work.api.impl.ScryfallApi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiRegistry {
    private final Map<String, ApiDefinition> apis = new LinkedHashMap<>();

    public ApiRegistry() {
        register(new JokeApi());
        register(new ScryfallApi());
        register(new OpenMeteoApi());
    }

    private void register(ApiDefinition api) {
        apis.put(api.getApiName(), api);
    }

    public ApiDefinition get(String name) {
        return apis.get(name);
    }

    public List<ApiDefinition> getAll() {
        return List.copyOf(apis.values());
    }
}
