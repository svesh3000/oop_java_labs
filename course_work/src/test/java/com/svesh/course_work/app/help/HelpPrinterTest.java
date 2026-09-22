package com.svesh.course_work.app.help;

import com.svesh.course_work.api.ApiRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("HelpPrinter: prints general help")
class HelpPrinterTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void redirectStdout() {
        originalOut = System.out;
        System.setOut(new PrintStream(out, true));
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Prints help with flags, APIs and formats")
    void shouldPrintHelp() {
        HelpPrinter.printGeneralHelp(new ApiRegistry());
        String output = out.toString();

        assertTrue(output.contains("DATA AGGREGATOR"));
        assertTrue(output.contains("--automatic"));
        assertTrue(output.contains("--n"));
        assertTrue(output.contains("joke"));
        assertTrue(output.contains("json"));
    }
}
