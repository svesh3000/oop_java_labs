package com.svesh.lab6.abstract_program;

import java.util.Random;

public class AbstractProgram {
    private volatile ProgramStatus status = ProgramStatus.UNKNOWN;
    private final Object lock = new Object();

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

    public class Daemon extends Thread {
        private final long intervalMs;

        public Daemon(long intervalMs) {
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

    public void startDaemon(long intervalMs) {
        if (intervalMs <= 0) {
            throw new IllegalArgumentException("ERROR: Interval must be positive!");
        }
        Daemon daemon = new Daemon(intervalMs);
        daemon.start();
    }
}
