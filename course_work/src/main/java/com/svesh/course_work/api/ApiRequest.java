package com.svesh.course_work.api;

import java.util.Map;

public record ApiRequest(
        ApiDefinition api,
        Map<String, String> queryParams
) {
}
