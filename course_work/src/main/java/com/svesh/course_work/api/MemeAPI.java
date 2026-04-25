package com.svesh.course_work.api;

import java.util.Map;

public class MemeAPI implements ApiDefinition {

    @Override
    public String getApiName() {
        return "meme";
    }

    @Override
    public String getApiURL() {
        return "https://meme-api.com/gimme";
    }

    @Override
    public Map<String, String> getDefaultParams() {
        return Map.of("count", "1");
    }
}