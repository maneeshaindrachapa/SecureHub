package com.cloudpiercer.SecureHub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
class MethodSecurityIT {
    @Autowired
    MockMvc mvc;

    @Test
    void adminOnlyEndpointForbiddenForUser() throws Exception {
        mvc.perform(get("/admin/stats").with(user("bob").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminOnlyEndpointAllowedForAdmin() throws Exception {
        mvc.perform(get("/admin/stats").with(user("root").roles("ADMIN")))
                .andExpect(status().isOk());
    }

}
