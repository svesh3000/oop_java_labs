package com.svesh.lab1;

import com.svesh.lab1.hero.Hero;
import com.svesh.lab1.move_strategy.*;

public class Main {
    public static void main(String[] args) {
        Hero hero = new Hero("Edick");

        hero.move("Home", "City");

        hero.setMoveStrategy(new WalkStrategy());
        hero.move("Home", "City");

        hero.setMoveStrategy(new HorseStrategy());
        hero.move("City", "Saloon");

        hero.setMoveStrategy(new FlyStrategy());
        hero.move("Saloon", "Mountain");

        hero.setMoveStrategy(new SwimStrategy());
        hero.move("Mountain", "Lake");
    }
}
