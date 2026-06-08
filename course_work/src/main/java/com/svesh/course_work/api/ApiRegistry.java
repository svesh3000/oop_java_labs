package com.svesh.course_work.api;

import java.util.Collection;
import java.util.LinkedHashMap;
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

    public Collection<ApiDefinition> getAll() {
        return apis.values();
    }
}
