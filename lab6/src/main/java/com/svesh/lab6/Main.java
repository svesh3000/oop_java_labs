package com.svesh.lab6;

import com.svesh.lab6.abstract_program.AbstractProgram;
import com.svesh.lab6.supervisor.Supervisor;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        AbstractProgram program = new AbstractProgram();
        Supervisor supervisor = new Supervisor(program);
        supervisor.start();

        long interval = 2000;
        program.startDaemon(interval);

        Thread.sleep(4000);
        supervisor.runProgram();

        Thread.sleep(4000);
        supervisor.stopProgram();
    }
}
