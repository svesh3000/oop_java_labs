package com.svesh.lab1.move_strategy;

public class FlyStrategy implements MoveStrategy {
    @Override
    public String move(String from, String to) {
        return "Flying from " + from + " to " + to;
    }
}
