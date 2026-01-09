package com.svesh.lab1.hero;

import com.svesh.lab1.move_strategy.MoveStrategy;

public class Hero {
    private String name;
    private MoveStrategy moveStrategy;

    public Hero(String name) {
        this.name = name;
    }

    public void setMoveStrategy(MoveStrategy strategy) {
        moveStrategy = strategy;
    }

    public void move(String from, String to) {
        if (moveStrategy == null) {
            System.out.print(name + ": The transfer method is not selected!");
            return;
        }
        System.out.print(name + ": ");
        moveStrategy.move(from, to);
        System.out.print("!");
    }

    public String getName() {
        return name;
    }
}
