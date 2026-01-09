package com.svesh.lab1.move_strategy;

public class WalkStrategy implements MoveStrategy {
    @Override
    public void move(String from, String to) {
        System.out.println("Walking from " + from + " to " + to);
    }
}
