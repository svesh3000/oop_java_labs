package com.svesh.course_work.app;

import com.svesh.course_work.api.ApiRegistry;
import com.svesh.course_work.app.modes.AutomaticMode;
import com.svesh.course_work.app.modes.InteractiveMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("AppContext: creates application components")
class AppContextTest {
    @Test
    @DisplayName("Creates registry and both modes")
    void shouldCreateContext() {
        AppContext context = AppContext.create();

        assertInstanceOf(ApiRegistry.class, context.getApiRegistry());
        assertInstanceOf(AutomaticMode.class, context.getAutomaticMode());
        assertInstanceOf(InteractiveMode.class, context.getInteractiveMode());
    }
}
