package com.svesh.course_work.api.impl;

import com.svesh.course_work.api.ApiDefinition;

import java.util.Map;

public class ScryfallApi implements ApiDefinition {
    @Override
    public String getApiName() {
        return "scryfall";
    }

    @Override
    public String getApiUrl() {
        return "https://api.scryfall.com/cards/search";
    }

    @Override
    public Map<String, String> getDefaultQueryParams() {
        return Map.of("q", "dragon");
    }

    @Override
    public String getInstruction() {
        return """
                Scryfall API simple manual

                Description:
                    Provides Magic:The Gathering card data, including names, prices, images, and rules text.

                Examples of query parameters for search mode:
                    q=fireball
                    q=type:creature color:red
                    q=oracle:draw

                Default request:
                    cards/search
                    q=dragon
                """;
    }
}
