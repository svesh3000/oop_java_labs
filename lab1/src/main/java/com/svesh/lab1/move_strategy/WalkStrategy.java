package com.svesh.lab1.move_strategy;

public class WalkStrategy implements MoveStrategy {
    @Override
    public String move(String from, String to) {
        return "Walking from " + from + " to " + to;
    }
}
