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
                if (status == ProgramStatus.FATAL_ERROR) {
                    System.out.println("[SUPERVISOR]: " + status + " → terminate");
                    terminate();
                    return;
                }
                if (status == ProgramStatus.STOPPING) {
                    System.out.println("[SUPERVISOR]: " + status + " → restarting");
                    program.setStatus(ProgramStatus.RUNNING);
                    continue;
                }
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    terminate();
                    return;
                }
            }
        }
    }

    private void terminate() {
        active = false;
    }
}
