package com.svesh.lab1;

import com.svesh.lab1.hero.Hero;
import com.svesh.lab1.move_strategy.*;

public class Main {
    public static void main(String[] args) {
        Hero hero = new Hero("Edick");

        Condition[] conditions = {
                Condition.SHORT,
                Condition.LONG,
                Condition.HIGH,
                Condition.WATER,
        };

        for (Condition condition : conditions) {
            System.out.println("Condition: " + condition);

            switch (condition) {
                case SHORT -> hero.setMoveStrategy(new WalkStrategy());
                case LONG -> hero.setMoveStrategy(new HorseStrategy());
                case HIGH -> hero.setMoveStrategy(new FlyStrategy());
                case WATER -> hero.setMoveStrategy(new SwimStrategy());
            }

            hero.move("Home", "City");
            System.out.println();
        }
    }
}