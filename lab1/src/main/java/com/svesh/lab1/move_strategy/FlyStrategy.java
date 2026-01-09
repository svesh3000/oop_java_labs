package com.svesh.lab1.move_strategy;

public class FlyStrategy implements MoveStrategy {
    @Override
    public void move(String from, String to) {
        System.out.print("Flying from " + from + " to " + to);
    }
}
