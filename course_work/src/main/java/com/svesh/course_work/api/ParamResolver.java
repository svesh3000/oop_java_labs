package com.svesh.course_work.api;

import java.util.HashMap;
import java.util.Map;

public final class ParamResolver {
    private ParamResolver() {
    }

    public record Result(Map<String, String> params, String error) {
        public boolean isSuccess() {
            return error == null;
        }

        public static Result success(Map<String, String> params) {
            return new Result(params, null);
        }

        public static Result failure(String error) {
            return new Result(null, error);
        }
    }

    public static Result resolve(ApiDefinition api, Map<String, String> params) {
        Map<String, String> merged = new HashMap<>(api.getDefaultQueryParams());
        merged.putAll(params);

        String error = validate(api, merged);
        if (error != null) {
            return Result.failure(error);
        }
        return Result.success(merged);
    }

    private static String validate(ApiDefinition api, Map<String, String> params) {
        Map<String, ParamSpec> specs = api.getParamSpecs();

        for (String key : params.keySet()) {
            if (!specs.containsKey(key)) {
                return "Unknown parameter: '" + key + "'. Allowed: " + specs.keySet();
            }
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            ParamSpec spec = specs.get(entry.getKey());
            if (spec.isFreeForm()) {
                continue;
            }
            String value = entry.getValue();
            if (spec.multiValue()) {
                for (String part : value.split(",")) {
                    String v = part.trim();
                    if (v.isEmpty()) {
                        return "Empty value in '" + entry.getKey() + "' list.";
                    }
                    if (!spec.allowed().contains(v)) {
                        return "Unknown value '" + v + "' for parameter '" + entry.getKey()
                                + "'. Allowed: " + spec.allowed();
                    }
                }
            } else if (!spec.allowed().contains(value)) {
                return "Unknown value '" + value + "' for parameter '" + entry.getKey()
                        + "'. Allowed: " + spec.allowed();
            }
        }

        return null;
    }
}
