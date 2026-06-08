package com.svesh.course_work.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.svesh.course_work.api.ApiRequest;
import com.svesh.course_work.file_records.DataRecord;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Aggregator {
    private final HttpService httpService;
    private final JsonReceiver jsonReceiver;

    public Aggregator(HttpService httpService, JsonReceiver jsonReceiver) {
        this.httpService = httpService;
        this.jsonReceiver = jsonReceiver;
    }

    public List<DataRecord> aggregate(List<ApiRequest> requests) throws IOException {
        List<DataRecord> dataRecords = new ArrayList<>(requests.size());
        int id = 0;
        for (ApiRequest req : requests) {
            id++;
            String response = httpService.fetch(req);
            JsonNode data = jsonReceiver.parse(response);
            dataRecords.add(new DataRecord(id, req.api().getApiName(), Instant.now(), data));
        }
        return dataRecords;
    }
}
