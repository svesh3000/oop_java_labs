package com.svesh.course_work;

import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.modes.AutomaticMode;
import com.svesh.course_work.modes.InteractiveMode;

public class Main {
    public static void main(String[] args) {
        ApiRegistry apiReg = new ApiRegistry();
        String mode = args[0];
        switch (mode) {
            case "--automatic" -> AutomaticMode.play(args, apiReg);
            case "--interactive" -> InteractiveMode.play(args, apiReg);
        }
    }
}
