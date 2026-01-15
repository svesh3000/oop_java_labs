package com.svesh.lab1;

import com.svesh.lab1.hero.Hero;
import com.svesh.lab1.move_strategy.StrategyFactory;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Hero hero = new Hero();
        String condition = "";

        while (true) {
            System.out.print("Enter condition (short, long, high, water) or 'exit': ");
            condition = scanner.nextLine();

            if (condition.equals("exit")) {
                break;
            }

            if (!StrategyFactory.isValid(condition)) {
                System.out.println("Error: Unknown condition. Use: short, long, high, water.");
                continue;
            }

            System.out.print("Enter start point: ");
            String from = scanner.nextLine();
            System.out.print("Enter end point: ");
            String to = scanner.nextLine();

            hero.setMoveStrategy(StrategyFactory.set(condition));
            String result = hero.move(from, to);

            System.out.println(result);
        }

        scanner.close();
    }
}
