package com.svesh.course_work.api.impl;

import com.svesh.course_work.api.ApiDefinition;
import com.svesh.course_work.api.ParamSpec;

import java.util.Map;
import java.util.Set;

public class JokeApi implements ApiDefinition {
    @Override
    public String getApiName() {
        return "joke";
    }

    @Override
    public String getApiUrl() {
        return "https://v2.jokeapi.dev/joke/Any";
    }

    @Override
    public Map<String, ParamSpec> getParamSpecs() {
        return Map.of(
                "type", ParamSpec.optional(Set.of("single", "twopart")),
                "blacklistFlags", ParamSpec.optionalMulti(Set.of(
                        "nsfw", "religious", "political", "racist", "sexist", "explicit")),
                "contains", ParamSpec.freeOptional()
        );
    }

    @Override
    public Map<String, String> getDefaultQueryParams() {
        return Map.of();
    }

    @Override
    public String getInstruction() {
        return """
                JokeAPI simple manual
                
                Query parameters:
                    type            // "single" or "twopart"
                    blacklistFlags  // nsfw, religious, political, racist, sexist, explicit
                    contains        // optional keyword filter
                
                Example of adding query parameters:
                    blacklistFlags=racist
                """;
    }
}
