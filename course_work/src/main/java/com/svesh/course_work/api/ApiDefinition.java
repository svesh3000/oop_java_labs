package com.svesh.course_work.api;

import java.util.Map;

public interface ApiDefinition {
    String getApiName();

    String getApiUrl();

    Map<String, ParamSpec> getParamSpecs();

    Map<String, String> getDefaultQueryParams();

    String getInstruction();
}
