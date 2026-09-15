package com.svesh.course_work.api.impl;

import com.svesh.course_work.api.ApiDefinition;
import com.svesh.course_work.api.ParamSpec;

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
    public Map<String, ParamSpec> getParamSpecs() {
        return Map.of("q", ParamSpec.freeRequired());
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
                
                Required request parameter (default value used if not specified):
                    q=dragon
                
                Examples of query parameters:
                    q=fireball
                    q=oracle:draw
                """;
    }
}
