package com.svesh.course_work.api;

import java.util.Map;
import java.util.Optional;

public class JokeAPI implements ApiDefinition {
    @Override
    public String getApiName() {
        return "joke";
}

    @Override
    public String getApiURL() {
        return "https://v2.jokeapi.dev";
    }

    @Override
    public Optional<String> getDefaultEndpoint() {
        return Optional.of("joke");
    }

    @Override
    public Map<String, String> getDefaultPathParams() {
        return Map.of(
                "category", "Any"
        );
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

                Example of endpoint:
                    joke

                Path parameters for joke:
                    category
                        Any, Programming, Misc, Dark, Pun, Spooky, Christmas
                
                Example of adding path parameters:
                    category=Dark

                Query parameters for joke:
                    type            // "single" or "twopart"
                    blacklistFlags  // nsfw, religious, political, racist, sexist, explicit
                    contains        // optional keyword filter
                
                Example of adding query parameters:
                    blacklistFlags=racist
                
                Default:
                    joke
                    category=Any
                    type=single

                Docs:
                    https://v2.jokeapi.dev
                """;
    }
}
