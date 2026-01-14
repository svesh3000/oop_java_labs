package com.svesh.lab1.move_strategy;

public class SwimStrategy implements MoveStrategy {
    @Override
    public String move(String from, String to) {
        return "Swimming from " + from + " to " + to;
    }
}
