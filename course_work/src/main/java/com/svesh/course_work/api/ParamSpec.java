package com.svesh.course_work.api;

import java.util.Set;

public record ParamSpec(
        boolean required,
        boolean multiValue,
        Set<String> allowed
) {
    public boolean isFreeForm() {
        return allowed == null;
    }

    public static ParamSpec optional(Set<String> allowed) {
        return new ParamSpec(false, false, allowed);
    }
    public static ParamSpec optionalMulti(Set<String> allowed) {
        return new ParamSpec(false, true, allowed);
    }
    public static ParamSpec freeOptional() {
        return new ParamSpec(false, false, null);
    }
    public static ParamSpec freeRequired() {
        return new ParamSpec(true, false, null);
    }
}
