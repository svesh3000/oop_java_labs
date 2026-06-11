package com.svesh.course_work.io.filters;

public interface Filter<T> {
    T filterBySource(T data, String source);
}
