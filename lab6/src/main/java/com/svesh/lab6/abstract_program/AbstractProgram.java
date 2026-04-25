package com.svesh.lab6.abstract_program;

import java.util.Random;

public class AbstractProgram extends Thread {
    private volatile ProgramStatus status = ProgramStatus.UNKNOWN;
    private final Object lock = new Object();
    private final long intervalMs;

    public AbstractProgram(long intervalMs) {
        if (intervalMs <= 0) {
            throw new IllegalArgumentException("ERROR: Interval must be positive!");
        }
        this.intervalMs = intervalMs;
    }

    @Override
    public void run() {
        Daemon daemon = new Daemon(intervalMs);
        daemon.start();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(Long.MAX_VALUE);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            daemon.interrupt();
        }
    }

    public ProgramStatus getStatus() {
        return status;
    }

    public void setStatus(ProgramStatus newStatus) {
        synchronized (lock) {
            status = newStatus;
            lock.notifyAll();
        }
    }

    public Object getLock() {
        return lock;
    }

    private class Daemon extends Thread {
        private final long intervalMs;

        private Daemon(long intervalMs) {
            this.intervalMs = intervalMs;
            setDaemon(true);
        }

        @Override
        public void run() {
            Random random = new Random();
            while (true) {
                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                ProgramStatus[] values = ProgramStatus.values();
                ProgramStatus newStatus = values[random.nextInt(values.length)];
                setStatus(newStatus);
            }
        }
    }
}
