package com.svesh.lab6.abstract_program;

import java.util.Random;

public class AbstractProgram {
    private volatile ProgramStatus status = ProgramStatus.UNKNOWN;

    public class Daemon extends Thread {
        private final long intervalMs;
        private final Random random = new Random();

        public Daemon(long intervalMs) {
            this.intervalMs = intervalMs;
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(intervalMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                ProgramStatus[] values = ProgramStatus.values();
                status = values[random.nextInt(values.length)];
            }
        }
    }

    public void startDaemon(long intervalMs) {
        Daemon daemon = new Daemon(intervalMs);
        daemon.start();
    }
}