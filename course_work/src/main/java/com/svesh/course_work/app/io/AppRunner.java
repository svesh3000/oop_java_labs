package com.svesh.course_work.app.io;

import com.svesh.course_work.api.ApiRequest;
import com.svesh.course_work.ingest.IngestService;
import com.svesh.course_work.io.OutputFormat;
import com.svesh.course_work.io.filters.FilterFactory;
import com.svesh.course_work.io.storage.StorageFactory;
import com.svesh.course_work.io.storage.WriteMode;
import com.svesh.course_work.io.viewer.ViewerFactory;
import com.svesh.course_work.models.DataRecord;
import com.svesh.course_work.parallel.PollingService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class AppRunner {
    private final IngestService ingestService;
    private final StorageFactory storageFactory;
    private final ViewerFactory viewerFactory;
    private final FilterFactory filterFactory;
    private PollingService pollingService;

    public AppRunner(
            IngestService ingestService,
            StorageFactory storageFactory,
            ViewerFactory viewerFactory,
            FilterFactory filterFactory
    ) {
        this.ingestService = ingestService;
        this.storageFactory = storageFactory;
        this.viewerFactory = viewerFactory;
        this.filterFactory = filterFactory;
    }

    public void export(List<ApiRequest> requests, OutputFormat format,
                       Path path, WriteMode mode) throws IOException {
        var storage = storageFactory.create(format);
        List<DataRecord> records;
        int nextId = mode == WriteMode.APPEND ? storage.getMaxId(path) + 1 : 1;
        records = ingestService.aggregate(requests, nextId);
        storage.write(records, path, mode);
    }

    public void viewAll(OutputFormat format, Path path) {
        var storage = storageFactory.create(format);
        var viewer = viewerFactory.create(format);

        var data = storage.read(path);
        viewer.show(data);
    }

    public void viewBySource(OutputFormat format, Path path, String source) {
        var storage = storageFactory.create(format);
        var viewer = viewerFactory.create(format);
        var filter = filterFactory.create(format);

        var data = storage.read(path);
        var filtered = filter.filterBySource(data, source);
        viewer.show(filtered);
    }

    public void startPolling(List<ApiRequest> requests,
                             OutputFormat format,
                             Path path,
                             int maxThreads,
                             int intervalSeconds,
                             WriteMode mode) {
        if (isPollingRunning()) {
            throw new IllegalStateException("Polling is already running");
        }
        pollingService = new PollingService(ingestService, storageFactory, format, path);
        pollingService.start(requests, maxThreads, intervalSeconds, mode);
    }

    public void stopPolling() {
        if (isPollingRunning()) {
            pollingService.stop();
        }
    }

    public boolean isPollingRunning() {
        return pollingService != null && pollingService.isRunning();
    }

    public void awaitPollingShutdown() throws InterruptedException {
        if (pollingService != null) {
            pollingService.awaitShutdown();
        }
    }
}
