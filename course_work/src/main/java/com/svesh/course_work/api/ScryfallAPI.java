package com.svesh.course_work.api;

import java.util.Map;

public class ScryfallAPI implements ApiDefinition {

    @Override
    public String getApiName() {
        return "scryfall";
    }

    @Override
    public String getApiURL() {
        return "https://api.scryfall.com/cards/random";
    }

    @Override
    public Map<String, String> getDefaultParams() {
        return Map.of();
    }
}