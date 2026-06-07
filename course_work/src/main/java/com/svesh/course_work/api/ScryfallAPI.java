package com.svesh.course_work.api;

import java.util.Map;
import java.util.Optional;

public class ScryfallAPI implements ApiDefinition {
    @Override
    public String getApiName() {
        return "scryfall";
    }

    @Override
    public String getApiURL() {
        return "https://api.scryfall.com";
    }

    @Override
    public Optional<String> getDefaultEndpoint() {
        return Optional.of("cards/random");
    }

    @Override
    public Map<String, String> getDefaultPathParams() {
        return Map.of();
    }

    @Override
    public Map<String, String> getDefaultQueryParams() {
        return Map.of();
    }

    @Override
    public String getInstruction() {
        return """
                Scryfall API simple manual

                Description:
                    Provides Magic:The Gathering card data, including names, prices, images, and rules text.

                Examples of endpoints: request types:
                    1) cards/random //Random card
                    2) cards/search //Search cards
                
                Example choosing endpoint:
                    cards/search

                Examples of query parameters for search mode:
                    q=fireball
                    q=type:creature color:red
                    q=oracle:draw

                Default request:
                    cards/random

                Docs:
                    docs: https://scryfall.com/docs/api
                """;
    }
}
