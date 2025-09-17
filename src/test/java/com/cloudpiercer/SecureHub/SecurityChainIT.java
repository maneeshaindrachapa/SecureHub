package com.cloudpiercer.SecureHub;

import com.cloudpiercer.SecureHub.model.AppUser;
import com.cloudpiercer.SecureHub.repository.AppUserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityChainIT {
    @Autowired
    MockMvc mvc;
    @Autowired
    AppUserRepository repo;
    @Autowired
    PasswordEncoder enc;

    @BeforeEach
    void seed() {
        repo.deleteAll();
        AppUser u = new AppUser();
        u.setUsername("alice");
        u.setPasswordHash(enc.encode("s3cr3t"));
        u.setRole("USER");
        repo.save(u);
        AppUser a = new AppUser();
        a.setUsername("admin");
        a.setPasswordHash(enc.encode("admin"));
        a.setRole("ADMIN");
        repo.save(a);
    }

    @Test
    void publicEndpointIsOpen() throws Exception {
        mvc.perform(get("/public/ping")).andExpect(status().isOk());
    }
}
