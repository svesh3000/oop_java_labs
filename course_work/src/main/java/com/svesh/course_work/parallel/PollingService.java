package com.svesh.course_work.parallel;

import com.svesh.course_work.api.ApiRequest;
import com.svesh.course_work.ingest.IngestService;
import com.svesh.course_work.io.OutputFormat;
import com.svesh.course_work.io.storage.Storage;
import com.svesh.course_work.io.storage.StorageFactory;
import com.svesh.course_work.io.storage.WriteMode;
import com.svesh.course_work.models.DataRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PollingService {
    private static final int BATCH_SIZE = 10;
    private static final long QUEUE_POLL_MS = 200;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 20;

    private final IngestService ingestService;
    private final StorageFactory storageFactory;
    private final OutputFormat format;
    private final Path path;

    private ScheduledExecutorService scheduler;
    private ExecutorService writerPool;
    private BlockingQueue<DataRecord> queue;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger nextId = new AtomicInteger(1);

    private CountDownLatch shutdownLatch = new CountDownLatch(1);

    public PollingService(IngestService ingestService,
                          StorageFactory storageFactory,
                          OutputFormat format,
                          Path path) {
        this.ingestService = ingestService;
        this.storageFactory = storageFactory;
        this.format = format;
        this.path = path;
    }

    public boolean isRunning() {
        return running.get();
    }

    public synchronized void start(List<ApiRequest> requests,
                                   int maxThreads,
                                   int intervalSeconds,
                                   WriteMode mode) {
        if (running.get()) {
            throw new IllegalStateException("PollingService is already running");
        }
        if (maxThreads < 1) {
            throw new IllegalArgumentException("maxThreads must be >= 1");
        }
        if (intervalSeconds < 0) {
            throw new IllegalArgumentException("intervalSeconds must be >= 0");
        }

        Storage<?> storage = storageFactory.create(format);
        int initialId = (mode == WriteMode.APPEND ? storage.getMaxId(path) : 0) + 1;
        nextId.set(initialId);

        queue = new LinkedBlockingQueue<>();
        shutdownLatch = new CountDownLatch(1);
        running.set(true);

        writerPool = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "polling-writer");
            t.setDaemon(true);
            return t;
        });
        writerPool.submit(new RecordWriter());

        scheduler = Executors.newScheduledThreadPool(maxThreads, r -> {
            Thread t = new Thread(r, "polling-worker");
            t.setDaemon(true);
            return t;
        });

        for (ApiRequest req : requests) {
            scheduleFirstRun(req, intervalSeconds);
        }
    }

    public synchronized void stop() {
        if (!running.getAndSet(false)) {
            return;
        }

        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (writerPool != null) {
            writerPool.shutdown();
            try {
                if (!writerPool.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    writerPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                writerPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        shutdownLatch.countDown();
    }

    public void awaitShutdown() throws InterruptedException {
        CountDownLatch latch = shutdownLatch;
        if (latch != null) {
            latch.await();
        }
    }

    private void scheduleFirstRun(ApiRequest req, int intervalSeconds) {
        if (!running.get()) {
            return;
        }
        scheduler.schedule(() -> pollOnce(req, intervalSeconds), 0, TimeUnit.MILLISECONDS);
    }

    private void pollOnce(ApiRequest req, int intervalSeconds) {
        if (!running.get()) {
            return;
        }
        try {
            int id = nextId.getAndIncrement();
            DataRecord record = ingestService.fetchOne(req, id);
            queue.put(record);
            System.out.print("\n[polling] Fetched " + req.api().getApiName() + " (#" + id + ")");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } catch (IOException e) {
            System.err.println("[polling] " + req.api().getApiName() + " IO error: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("[polling] " + req.api().getApiName() + " error: " + e.getMessage());
        }

        if (running.get()) {
            scheduler.schedule(() -> pollOnce(req, intervalSeconds),
                    intervalSeconds, TimeUnit.SECONDS);
        }
    }


    private class RecordWriter implements Runnable {
        @Override
        public void run() {
            Storage<?> storage = storageFactory.create(format);
            List<DataRecord> batch = new ArrayList<>(BATCH_SIZE);
            while (running.get() || !queue.isEmpty()) {
                try {
                    DataRecord first = queue.poll(QUEUE_POLL_MS, TimeUnit.MILLISECONDS);
                    if (first == null) {
                        continue;
                    }
                    batch.add(first);
                    queue.drainTo(batch, BATCH_SIZE - 1);
                    storage.write(batch, path, WriteMode.APPEND);
                    batch.clear();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } catch (RuntimeException e) {
                    System.err.println("[polling-writer] Write failed: " + e.getMessage());
                    batch.clear();
                }
            }
        }
    }
}
