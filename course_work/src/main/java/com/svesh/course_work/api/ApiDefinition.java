package com.svesh.course_work.api;

import java.util.Map;

public interface ApiDefinition {
    String getApiName();

    String getApiURL();

    Map<String, String> getDefaultParams();
}
