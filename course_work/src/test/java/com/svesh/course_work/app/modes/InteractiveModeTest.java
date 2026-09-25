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
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InteractiveMode: menu and flows")
class InteractiveModeTest {
    @Mock
    private AppRunner runner;

    private final ApiRegistry registry = new ApiRegistry();

    private PrintStream originalOut;
    private ByteArrayOutputStream out;

    @BeforeEach
    void redirectStdout() {
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true));
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    private String runWith(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        new InteractiveMode(runner, registry, scanner).run();
        return out.toString();
    }

    @Test
    @DisplayName("Reports invalid menu option")
    void shouldReportInvalidOption() {
        assertTrue(runWith("9\n0\n").contains("Invalid option"));
    }

    @Test
    @DisplayName("Exits on 0")
    void shouldExitOnZero() {
        assertTrue(runWith("0\n").contains("EXIT"));
    }

    @Test
    @DisplayName("Export: new file → CREATE, EXPORT COMPLETED")
    void shouldExportToNewFile(@TempDir Path dir) throws IOException {
        String path = dir.resolve("out").toString();
        String output = runWith("1\njoke\n\n1\n" + path + "\n0\n");

        assertTrue(output.contains("EXPORT COMPLETED"));
        verify(runner).export(anyList(), eq(OutputFormat.JSON), any(Path.class), eq(WriteMode.CREATE));
    }

    @Test
    @DisplayName("Export: existing file → OVERWRITE")
    void shouldOverwriteExistingFile(@TempDir Path dir) throws IOException {
        Files.createFile(dir.resolve("out.json"));
        runWith("1\njoke\n\n1\n" + dir.resolve("out") + "\n1\n0\n");

        verify(runner).export(anyList(), eq(OutputFormat.JSON), any(Path.class), eq(WriteMode.CREATE));
    }

    @Test
    @DisplayName("Export: existing file → APPEND")
    void shouldAppendToExistingFile(@TempDir Path dir) throws IOException {
        Files.createFile(dir.resolve("out.json"));
        runWith("1\njoke\n\n1\n" + dir.resolve("out") + "\n2\n0\n");

        verify(runner).export(anyList(), eq(OutputFormat.JSON), any(Path.class), eq(WriteMode.APPEND));
    }

    @Test
    @DisplayName("Export: existing file → CANCEL")
    void shouldCancelOnFileAction(@TempDir Path dir) throws IOException {
        Files.createFile(dir.resolve("out.json"));
        String output = runWith("1\njoke\n\n1\n" + dir.resolve("out") + "\n\n0\n");

        assertTrue(output.contains("OPERATION CANCELLED"));
        verify(runner, never()).export(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Export: existing file → CHANGE_PATH → cancel")
    void shouldChangePathThenCancel(@TempDir Path dir) throws IOException {
        Files.createFile(dir.resolve("out.json"));
        String output = runWith("1\njoke\n\n1\n" + dir.resolve("out") + "\n3\n\n0\n");

        assertTrue(output.contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Export: IO error is reported")
    void shouldReportIoError(@TempDir Path dir) throws IOException {
        doThrow(new IOException("disk full"))
                .when(runner).export(any(), any(), any(), any());

        String path = dir.resolve("out").toString();
        String output = runWith("1\njoke\n\n1\n" + path + "\n0\n");

        assertTrue(output.contains("EXPORT FAILED"));
    }

    @Test
    @DisplayName("Export: cancel on empty API name")
    void shouldCancelOnEmptyApi() {
        assertTrue(runWith("1\n\n0\n").contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Export: cancel on 'cancel' in params")
    void shouldCancelOnParamsCancel() {
        assertTrue(runWith("1\njoke\ncancel\n0\n").contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Export: cancel on empty format")
    void shouldCancelOnEmptyFormat() {
        assertTrue(runWith("1\njoke\n\n\n0\n").contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Export: cancel on empty path")
    void shouldCancelOnEmptyPath() {
        assertTrue(runWith("1\njoke\n\n1\n\n0\n").contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Export: rejects multiple API names when only one allowed")
    void shouldRejectMultipleApis() {
        String output = runWith("1\njoke scryfall\njoke\n\n1\n/tmp/x\n0\n");
        assertTrue(output.contains("Only one API is expected"));
        assertTrue(output.contains("EXPORT COMPLETED"));
    }

    @Test
    @DisplayName("Export: retries on unknown API name")
    void shouldRetryOnUnknownApi() {
        String output = runWith("1\nnope\njoke\n\n1\n/tmp/x\n0\n");
        assertTrue(output.contains("Unknown API"));
        assertTrue(output.contains("EXPORT COMPLETED"));
    }

    @Test
    @DisplayName("collectParams: invalid format retry")
    void shouldRetryInvalidParamFormat(@TempDir Path dir) {
        String path = dir.resolve("out").toString();
        String output = runWith("1\njoke\nbadpair\n\n1\n" + path + "\n0\n");
        assertTrue(output.contains("Invalid format"));
    }

    @Test
    @DisplayName("collectParams: duplicate key rejected")
    void shouldRejectDuplicateKey(@TempDir Path dir) {
        String path = dir.resolve("out").toString();
        String output = runWith("1\njoke\ntype=single type=twopart\n\n1\n" + path + "\n0\n");
        assertTrue(output.contains("Duplicate parameter"));
    }

    @Test
    @DisplayName("collectParams: unknown parameter rejected")
    void shouldRejectUnknownParam(@TempDir Path dir) {
        String path = dir.resolve("out").toString();
        String output = runWith("1\njoke\nfoo=bar\n\n1\n" + path + "\n0\n");
        assertTrue(output.contains("Unknown parameter"));
    }

    @Test
    @DisplayName("viewAll: cancel on empty path")
    void shouldCancelViewAll() {
        assertTrue(runWith("2\n\n0\n").contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("viewAll: file not found")
    void shouldReportFileNotFound(@TempDir Path dir) {
        String output = runWith("2\n" + dir.resolve("notfound.json") + "\n0\n");
        assertTrue(output.contains("File not found"));
    }

    @Test
    @DisplayName("viewAll: existing file is printed")
    void shouldViewExistingFile(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");
        Files.createFile(file);

        String output = runWith("2\n" + file + "\n0\n");

        assertTrue(output.contains("OUTPUT START"));
        assertTrue(output.contains("OUTPUT END"));
        verify(runner).viewAll(eq(OutputFormat.JSON), eq(file));
    }

    @Test
    @DisplayName("viewAll: runtime error inside viewer is reported")
    void shouldReportViewError(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");
        Files.createFile(file);
        doThrow(new RuntimeException("boom")).when(runner).viewAll(any(), any());

        String output = runWith("2\n" + file + "\n0\n");
        assertTrue(output.contains("Error: boom"));
    }

    @Test
    @DisplayName("viewAll: trailing slash rejected, then valid path")
    void shouldRejectTrailingSlash(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");
        Files.createFile(file);

        String output = runWith("2\n/tmp/\n" + file + "\n0\n");
        assertTrue(output.contains("Path is a directory"));
    }

    @Test
    @DisplayName("viewAll: unknown extension → choose JSON → resolved path not found")
    void shouldPromptFormatForUnknownExtension(@TempDir Path dir) throws IOException {
        Files.createFile(dir.resolve("noext"));
        String output = runWith("2\n" + dir.resolve("noext") + "\n1\n0\n");

        assertTrue(output.contains("Unknown format"));
        assertTrue(output.contains("File not found"));
    }

    @Test
    @DisplayName("viewAll: unknown format → cancel")
    void shouldCancelOnUnknownFormat() {
        String output = runWith("2\n/nonexistent/noext\n\n0\n");
        assertTrue(output.contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("viewBySource: filters by source")
    void shouldViewBySource(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");
        Files.createFile(file);

        String output = runWith("3\n" + file + "\njoke\n0\n");

        assertTrue(output.contains("OUTPUT START"));
        verify(runner).viewBySource(eq(OutputFormat.JSON), eq(file), eq("joke"));
    }

    @Test
    @DisplayName("viewBySource: cancel on empty source")
    void shouldCancelOnEmptySource(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("out.json");
        Files.createFile(file);

        String output = runWith("3\n" + file + "\n\n0\n");
        assertTrue(output.contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Format: rejects invalid, then accepts")
    void shouldRejectInvalidFormatThenAccept(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("noext");
        Files.createFile(file);

        String output = runWith("2\n" + file + "\n9\n1\n0\n");

        assertTrue(output.contains("Please enter 1 or 2"));
    }

    @Test
    @DisplayName("Polling: cancel on empty API")
    void shouldCancelPollingOnEmptyApi() {
        assertTrue(runWith("4\n\n0\n").contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Polling: cancel on empty format")
    void shouldCancelPollingOnFormat() {
        assertTrue(runWith("4\njoke\n\n0\n").contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Polling: cancel on empty path")
    void shouldCancelPollingOnPath() {
        assertTrue(runWith("4\njoke\n1\n\n0\n").contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Polling: cancel on empty n")
    void shouldCancelPollingOnEmptyN(@TempDir Path dir) {
        String path = dir.resolve("out").toString();
        String output = runWith("4\njoke\n1\n" + path + "\n\n0\n");
        assertTrue(output.contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Polling: cancel on empty t")
    void shouldCancelPollingOnEmptyT(@TempDir Path dir) {
        String path = dir.resolve("out").toString();
        String output = runWith("4\njoke\n1\n" + path + "\n2\n\n0\n");
        assertTrue(output.contains("OPERATION CANCELLED"));
    }

    @Test
    @DisplayName("Polling: rejects non-integer n, retries, then succeeds")
    void shouldRetryNonIntegerN(@TempDir Path dir) {
        String path = dir.resolve("out").toString();
        String output = runWith("4\njoke\n1\n" + path + "\nabc\n2\n3\n0\n");

        assertTrue(output.contains("Please enter an integer"));
        verify(runner).startPolling(anyList(), any(), any(), eq(2), eq(3), any());
    }

    @Test
    @DisplayName("Polling: refuses to start when already running")
    void shouldRefuseWhenPollingRunning() {
        when(runner.isPollingRunning()).thenReturn(true);

        String output = runWith("4\n0\n");
        assertTrue(output.contains("Polling is already running"));
        verify(runner, never()).startPolling(any(), any(), any(), anyInt(), anyInt(), any());
    }

    @Test
    @DisplayName("Polling: starts successfully")
    void shouldStartPolling(@TempDir Path dir) {
        String path = dir.resolve("out").toString();
        String output = runWith("4\njoke\n1\n" + path + "\n2\n3\n0\n");

        assertTrue(output.contains("Polling started"));
        verify(runner).startPolling(
                anyList(), eq(OutputFormat.JSON), any(Path.class), eq(2), eq(3), eq(WriteMode.CREATE)
        );
    }

    @Test
    @DisplayName("Polling: error on start is reported")
    void shouldReportStartError(@TempDir Path dir) {
        doThrow(new IllegalStateException("boom"))
                .when(runner).startPolling(any(), any(), any(), anyInt(), anyInt(), any());

        String path = dir.resolve("out").toString();
        String output = runWith("4\njoke\n1\n" + path + "\n2\n3\n0\n");
        assertTrue(output.contains("Error:"));
    }

    @Test
    @DisplayName("Stop polling: not running")
    void shouldReportStopWhenNotRunning() {
        String output = runWith("5\n0\n");
        assertTrue(output.contains("Polling is not running"));
    }

    @Test
    @DisplayName("Stop polling: running → stops")
    void shouldStopPolling() {
        when(runner.isPollingRunning()).thenReturn(true, false);

        String output = runWith("5\n0\n");
        assertTrue(output.contains("Polling stopped"));
        verify(runner).stopPolling();
    }
}
