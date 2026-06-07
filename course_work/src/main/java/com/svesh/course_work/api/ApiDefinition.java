package com.svesh.course_work.api;

import java.util.Map;
import java.util.Optional;

public interface ApiDefinition {
    String getApiName();

    String getApiURL();

    Optional<String> getDefaultEndpoint();

    Map<String, String> getDefaultPathParams();

    Map<String, String> getDefaultQueryParams();

    String getInstruction();
}
