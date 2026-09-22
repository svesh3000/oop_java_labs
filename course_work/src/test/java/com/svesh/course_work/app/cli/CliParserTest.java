package com.svesh.course_work.app.cli;

import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.app.io.OutputPathResolver;
import com.svesh.course_work.io.OutputFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CliParser: parses automatic-mode arguments")
class CliParserTest {
    private final CliParser parser = new CliParser(new ApiRegistry(), new OutputPathResolver());

    @Test
    @DisplayName("Parses minimal command with defaults for n and t")
    void shouldParseMinimal() {
        CliConfig config = parser.parse(new String[]{"--api", "joke", "--format", "json"});

        assertEquals(OutputFormat.JSON, config.format());
        assertEquals(List.of("joke"), config.apiNames());
        assertEquals(1, config.maxThreads());
        assertEquals(5, config.intervalSeconds());
        assertEquals("output.json", config.path().toString());
    }

    @Test
    @DisplayName("Parses all flags")
    void shouldParseAllFlags() {
        CliConfig config = parser.parse(new String[]{
                "--api", "joke", "scryfall",
                "--format", "csv",
                "--out", "result",
                "--n", "3",
                "--t", "10"
        });

        assertEquals(OutputFormat.CSV, config.format());
        assertEquals(List.of("joke", "scryfall"), config.apiNames());
        assertEquals("result.csv", config.path().toString());
        assertEquals(3, config.maxThreads());
        assertEquals(10, config.intervalSeconds());
    }

    @ParameterizedTest(name = "rejects with {0}")
    @MethodSource("errorCases")
    @DisplayName("Throws CliError with expected code")
    void shouldThrowWithExpectedCode(CliError.Code expectedCode, String[] args) {
        CliError error = assertThrows(CliError.class, () -> parser.parse(args));
        assertEquals(expectedCode, error.getCode());
    }

    static Stream<Arguments> errorCases() {
        return Stream.of(
                Arguments.of(CliError.Code.ERR_INVALID_ARG,
                        new String[]{"--api", "joke", "--format", "json", " "}),
                Arguments.of(CliError.Code.ERR_INVALID_ARG,
                        new String[]{"--api", "joke", "--format", "json", null}),

                Arguments.of(CliError.Code.ERR_EMPTY_API_LIST, new String[]{"--api"}),
                Arguments.of(CliError.Code.ERR_EMPTY_API_LIST,
                        new String[]{"--api", "--format", "json"}),
                Arguments.of(CliError.Code.ERR_UNKNOWN_API,
                        new String[]{"--api", "unknown", "--format", "json"}),

                Arguments.of(CliError.Code.ERR_CONFLICT_FORMAT,
                        new String[]{"--api", "joke", "--format", "json", "--format", "csv"}),
                Arguments.of(CliError.Code.ERR_NO_FORMAT,
                        new String[]{"--api", "joke", "--format"}),
                Arguments.of(CliError.Code.ERR_UNKNOWN_FORMAT,
                        new String[]{"--api", "joke", "--format", "xml"}),
                Arguments.of(CliError.Code.ERR_NO_FORMAT,
                        new String[]{"--api", "joke"}),

                Arguments.of(CliError.Code.ERR_CONFLICT_OUTPUT_PATH,
                        new String[]{"--api", "joke", "--format", "json", "--out", "a", "--out", "b"}),
                Arguments.of(CliError.Code.ERR_INVALID_OUTPUT_PATH,
                        new String[]{"--api", "joke", "--format", "json", "--out"}),
                Arguments.of(CliError.Code.ERR_INVALID_OUTPUT_PATH,
                        new String[]{"--api", "joke", "--format", "json", "--out", "out/"}),
                Arguments.of(CliError.Code.ERR_INVALID_OUTPUT_PATH,
                        new String[]{"--api", "joke", "--format", "json", "--out", "out\\"}),
                Arguments.of(CliError.Code.ERR_INVALID_OUTPUT_PATH,
                        new String[]{"--api", "joke", "--format", "json", "--out", "out|j.csv"}),

                Arguments.of(CliError.Code.ERR_CONFLICT_NUMBER_OF_THREADS,
                        new String[]{"--api", "joke", "--format", "json", "--n", "1", "--n", "2"}),
                Arguments.of(CliError.Code.ERR_INVALID_ARG,
                        new String[]{"--api", "joke", "--format", "json", "--n"}),
                Arguments.of(CliError.Code.ERR_INVALID_ARG,
                        new String[]{"--api", "joke", "--format", "json", "--n", "abc"}),
                Arguments.of(CliError.Code.ERR_INVALID_ARG,
                        new String[]{"--api", "joke", "--format", "json", "--n", "0"}),

                Arguments.of(CliError.Code.ERR_CONFLICT_INTERVAL,
                        new String[]{"--api", "joke", "--format", "json", "--t", "5", "--t", "10"}),
                Arguments.of(CliError.Code.ERR_INVALID_ARG,
                        new String[]{"--api", "joke", "--format", "json", "--t"}),
                Arguments.of(CliError.Code.ERR_INVALID_ARG,
                        new String[]{"--api", "joke", "--format", "json", "--t", "abc"}),
                Arguments.of(CliError.Code.ERR_INVALID_ARG,
                        new String[]{"--api", "joke", "--format", "json", "--t", "-1"}),

                Arguments.of(CliError.Code.ERR_UNKNOWN_FLAG,
                        new String[]{"--api", "joke", "--format", "json", "--foo"}),
                Arguments.of(CliError.Code.ERR_INVALID_ARG,
                        new String[]{"--api", "joke", "--format", "json", "extra"}),

                Arguments.of(CliError.Code.ERR_NO_APIS,
                        new String[]{"--format", "json"})
        );
    }
}
