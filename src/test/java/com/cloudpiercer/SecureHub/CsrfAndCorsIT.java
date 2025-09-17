package com.cloudpiercer.SecureHub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CsrfAndCorsIT {
    @Autowired
    MockMvc mvc;

    @Test
    void optionsPreflightAllowedByCors() throws Exception {
        mvc.perform(options("/public/ping")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());
    }

    @Test
    void csrfExample_whenEnabledWouldRequireToken() throws Exception {
        // In this project CSRF is disabled for stateless API; this test documents the
        // behavior.
        mvc.perform(post("/public/ping"))
                .andExpect(status().isForbidden());
    }

}
