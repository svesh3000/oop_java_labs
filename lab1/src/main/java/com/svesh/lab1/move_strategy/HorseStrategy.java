package com.svesh.lab1.move_strategy;

public class HorseStrategy implements MoveStrategy {
    @Override
    public String move(String from, String to) {
        return "Riding a horse from " + from + " to " + to;
    }
}
