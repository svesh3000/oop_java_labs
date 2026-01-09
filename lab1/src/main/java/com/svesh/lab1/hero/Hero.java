package com.svesh.lab1.hero;

import com.svesh.lab1.move_strategy.MoveStrategy;

public class Hero {
    private String name;
    private MoveStrategy moveStrategy;

    public Hero(String name) {
        this.name = name;
    }

    public void setMoveStrategy(MoveStrategy strategy) {
        this.moveStrategy = strategy;
    }

    public void move(String from, String to) {
        if (moveStrategy == null) {
            System.out.println(name + ": The transfer method is not selected!");
            return;
        }
        System.out.print(name + ": ");
        moveStrategy.move(from, to);
        System.out.println("!");
    }

    public String getName() {
        return name;
    }
}