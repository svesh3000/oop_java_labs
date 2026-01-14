package com.svesh.lab1.hero;

import com.svesh.lab1.move_strategy.MoveStrategy;

public class Hero {
    private MoveStrategy moveStrategy;

    public void setMoveStrategy(MoveStrategy strategy) {
        moveStrategy = strategy;
    }

    public String move(String from, String to) {
        if (moveStrategy == null) {
            return null;
        }
        return moveStrategy.move(from, to);
    }
}