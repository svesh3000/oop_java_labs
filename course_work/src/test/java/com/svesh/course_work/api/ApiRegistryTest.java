package com.svesh.course_work.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiRegistry: registration and lookup of APIs")
class ApiRegistryTest {
    @Test
    @DisplayName("ApiRegistry registers and retrieves APIs")
    void shouldRegisterAndRetrieveApis() {
        ApiRegistry registry = new ApiRegistry();

        assertEquals(3, registry.getAll().size());
        assertEquals("joke", registry.get("joke").getApiName());
        assertNull(registry.get("unknown"));
    }
}
