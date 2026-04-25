package com.svesh.lab6;

import com.svesh.lab6.abstract_program.AbstractProgram;
import com.svesh.lab6.supervisor.Supervisor;

public class Main {
    public static void main(String[] args) {
        long interval = 2000;
        AbstractProgram program = new AbstractProgram(interval);
        Supervisor supervisor = new Supervisor(program);

        program.start();
        supervisor.start();

        try {
            Thread.sleep(4000);
            supervisor.runProgram();

            Thread.sleep(4000);
            supervisor.stopProgram();

            supervisor.join();
        } catch (InterruptedException e) {
            System.out.println("INTERRUPTED!");
            supervisor.shutdown();
        }
    }
}
