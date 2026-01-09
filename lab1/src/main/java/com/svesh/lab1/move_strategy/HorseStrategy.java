package com.svesh.lab1.move_strategy;

public class HorseStrategy implements MoveStrategy {
    @Override
    public void move(String from, String to) {
        System.out.print("Riding a horse from " + from + " to " + to);
    }
}
