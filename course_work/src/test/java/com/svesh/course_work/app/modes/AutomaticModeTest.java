package com.svesh.course_work.app.modes;

import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.app.io.AppRunner;
import com.svesh.course_work.io.OutputFormat;
import com.svesh.course_work.io.storage.WriteMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutomaticMode: parses args and starts polling")
class AutomaticModeTest {
    @Mock
    private AppRunner runner;

    private final ApiRegistry registry = new ApiRegistry();
    private AutomaticMode mode;

    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream(), true));
        mode = new AutomaticMode(runner, registry);
    }

    @AfterEach
    void tearDown() {
        Thread.interrupted();
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Starts polling and returns 0 on valid arguments")
    void shouldStartPolling() {
        int code = mode.run(new String[]{
                "--api", "joke",
                "--format", "json",
                "--out", "out",
                "--n", "2",
                "--t", "3"
        });

        assertEquals(0, code);
        verify(runner).startPolling(
                anyList(),
                eq(OutputFormat.JSON),
                any(Path.class),
                eq(2),
                eq(3),
                eq(WriteMode.APPEND)
        );
    }

    @Test
    @DisplayName("Returns 2 on invalid CLI arguments")
    void shouldReturn2OnParseFailure() {
        int code = mode.run(new String[]{"--api", "joke"});

        assertEquals(2, code);
        verify(runner, never()).startPolling(any(), any(), any(), anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("Returns 1 when startPolling throws")
    void shouldReturn1WhenStartPollingFails() {
        doThrow(new IllegalStateException("Polling is already running"))
                .when(runner).startPolling(any(), any(), any(), anyInt(), anyInt(), any());

        int code = mode.run(new String[]{
                "--api", "joke",
                "--format", "json",
                "--out", "out"
        });

        assertEquals(1, code);
    }

    @Test
    @DisplayName("Restores interrupt flag when awaitPollingShutdown is interrupted")
    void shouldRestoreInterruptFlagOnInterrupt() throws InterruptedException {
        doThrow(new InterruptedException("test"))
                .when(runner).awaitPollingShutdown();

        int code = mode.run(new String[]{
                "--api", "joke",
                "--format", "json",
                "--out", "out"
        });

        assertEquals(0, code);
        assertTrue(Thread.currentThread().isInterrupted(), "InterruptedException");
    }
}
