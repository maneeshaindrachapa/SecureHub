package com.cloudpiercer.SecureHub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import com.cloudpiercer.SecureHub.service.JwtService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JwtFlowIT {
    @Autowired
    MockMvc mvc;
    @Autowired
    JwtService jwt;

    @Test
    void jwtAllowsAccessToProtectedEndpoint() throws Exception {
        String token = jwt.issue("alice", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        mvc.perform(get("/user/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value("alice"));
    }

}
