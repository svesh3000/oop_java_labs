package com.svesh.course_work;

import com.svesh.course_work.app.AppContext;
import com.svesh.course_work.app.help.HelpPrinter;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        AppContext context = AppContext.create();

        if (args.length == 0) {
            HelpPrinter.printGeneralHelp(context.getApiRegistry());
            return;
        }

        String[] modeArgs = Arrays.copyOfRange(args, 1, args.length);
        int exitCode;
        switch (args[0]) {
            case "--automatic" -> exitCode = context.getAutomaticMode().run(modeArgs);
            case "--interactive" -> {
                if (args.length > 1) {
                    System.err.println("Warning: extra arguments");
                }
                exitCode = context.getInteractiveMode().run();
            }
            case "--help" -> {
                if (args.length > 1) {
                    System.err.println("Warning: extra arguments");
                }
                HelpPrinter.printGeneralHelp(context.getApiRegistry());
                exitCode = 0;
            }
            default -> {
                System.err.println("ERROR: Unknown mode: " + args[0]);
                HelpPrinter.printGeneralHelp(context.getApiRegistry());
                exitCode = 2;
            }
        }

        System.exit(exitCode);
    }
}
