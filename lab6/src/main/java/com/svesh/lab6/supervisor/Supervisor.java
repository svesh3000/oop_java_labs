package com.svesh.lab6.supervisor;

import com.svesh.lab6.abstract_program.AbstractProgram;
import com.svesh.lab6.abstract_program.ProgramStatus;

public class Supervisor extends Thread {
    private final AbstractProgram program;
    private volatile boolean active;

    public Supervisor(AbstractProgram program) {
        this.program = program;
        active = true;
    }

    public void runProgram() {
        System.out.println("[SUPERVISOR]: set RUNNING");
        program.setStatus(ProgramStatus.RUNNING);
    }

    public void stopProgram() {
        System.out.println("[SUPERVISOR]: set STOPPING");
        program.setStatus(ProgramStatus.STOPPING);
    }

    @Override
    public void run() {
        Object lock = program.getLock();

        while (active) {
            synchronized (lock) {
                ProgramStatus status = program.getStatus();
                switch (status) {
                    case FATAL_ERROR:
                        System.out.println("[STATUS]: " + status + " → terminate");
                        active = false;
                        return;
                    case STOPPING:
                        System.out.println("[STATUS]: " + status + " → restarting");
                        program.setStatus(ProgramStatus.RUNNING);
                        break;
                    default:
                        System.out.println("[STATUS]: " + status);
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            active = false;
                            return;
                        }
                }
            }
        }
    }
}
