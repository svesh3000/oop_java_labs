package com.svesh.course_work.app.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CliError: stores code and message")
class CliErrorTest {
    @Test
    @DisplayName("Stores code and message")
    void shouldStoreCodeAndMessage() {
        CliError error = new CliError(CliError.Code.ERR_NO_APIS, "no apis");

        assertEquals(CliError.Code.ERR_NO_APIS, error.getCode());
        assertEquals("no apis", error.getMessage());
    }
}
