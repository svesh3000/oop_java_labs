package com.svesh.lab1.move_strategy;

public final class StrategyFactory {
    private StrategyFactory() {}

    public static MoveStrategy set(String condition) {
        return switch (condition) {
            case "short" -> new WalkStrategy();
            case "long" -> new HorseStrategy();
            case "high" -> new FlyStrategy();
            case "water" -> new SwimStrategy();
            default -> null;
        };
    }

    public static boolean isValid(String condition) {
        return switch (condition) {
            case "short", "long", "high", "water" -> true;
            default -> false;
        };
    }
}