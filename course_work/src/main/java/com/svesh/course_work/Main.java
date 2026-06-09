package com.svesh.course_work;

import com.svesh.course_work.app.modes.AutomaticMode;
import com.svesh.course_work.app.modes.InteractiveMode;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: --automatic | --interactive");
            return;
        }

        String mode = args[0];
        switch (mode) {
            case "--automatic" -> AutomaticMode.run(args);
            case "--interactive" -> InteractiveMode.run(args);
            default -> System.out.println("Unknown mode: " + mode);
        }
    }
}
