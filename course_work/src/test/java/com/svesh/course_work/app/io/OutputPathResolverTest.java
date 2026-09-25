package com.svesh.course_work.app.io;

import com.svesh.course_work.app.cli.CliError;
import com.svesh.course_work.io.OutputFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("OutputPathResolver: appends or checks extension")
class OutputPathResolverTest {
    private final OutputPathResolver resolver = new OutputPathResolver();

    @Test
    @DisplayName("Appends extension when missing")
    void shouldAppendExtension() {
        Path resolved = resolver.resolve(Path.of("res"), OutputFormat.JSON);
        assertEquals("res.json", resolved.toString());
    }

    @Test
    @DisplayName("Keeps matching extension")
    void shouldKeepMatchingExtension() {
        Path resolved = resolver.resolve(Path.of("res.csv"), OutputFormat.CSV);
        assertEquals("res.csv", resolved.toString());
    }

    @Test
    @DisplayName("Throws on mismatching extension")
    void shouldThrowOnMismatch() {
        assertThrows(CliError.class,
                () -> resolver.resolve(Path.of("res.csv"), OutputFormat.JSON));
    }

    @Test
    @DisplayName("Throws on root path")
    void shouldThrowOnRootPath() {
        assertThrows(CliError.class,
                () -> resolver.resolve(Path.of("/"), OutputFormat.JSON));
    }
}
