package com.svesh.lab1.move_strategy;

public class SwimStrategy implements MoveStrategy {
    @Override
    public void move(String from, String to) {
        System.out.println("Swimming from " + from + " to " + to);
    }
}
