package com.svesh.course_work.api;

import java.util.Map;

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
    public Map<String, String> getDefaultQueryParams() {
        return Map.of(
                "type", "single"
        );
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
                
                Default:
                    type=single
                """;
    }
}
