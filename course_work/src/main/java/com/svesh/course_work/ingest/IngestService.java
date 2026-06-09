package com.svesh.course_work.ingest;

import com.fasterxml.jackson.databind.JsonNode;
import com.svesh.course_work.api.ApiRequest;
import com.svesh.course_work.models.DataRecord;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class IngestService {
    private final HttpService httpService;
    private final JsonParser jsonParser;

    public IngestService(HttpService httpService, JsonParser jsonParser) {
        this.httpService = httpService;
        this.jsonParser = jsonParser;
    }

    public List<DataRecord> aggregate(List<ApiRequest> requests) throws IOException {
        List<DataRecord> dataRecords = new ArrayList<>(requests.size());
        int id = 0;
        for (ApiRequest req : requests) {
            id++;
            String response = httpService.fetch(req);
            JsonNode data = jsonParser.parse(response);
            dataRecords.add(new DataRecord(id, req.api().getApiName(), Instant.now(), data));
        }
        return dataRecords;
    }
}
