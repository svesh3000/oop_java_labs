package com.svesh.course_work.api;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiRegistry {
    private final Map<String, ApiDefinition> apis = new LinkedHashMap<>();

    public ApiRegistry() {
        register(new JokeAPI());
        register(new ScryfallAPI());
        register(new OpenMeteoAPI());
    }

    private void register(ApiDefinition api) {
        apis.put(api.getApiName(), api);
    }

    public ApiDefinition get(String name) {
        return apis.get(name);
    }

    public Collection<ApiDefinition> getAll() {
        return apis.values();
    }
}
