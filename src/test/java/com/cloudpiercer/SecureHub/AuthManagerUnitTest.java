package com.cloudpiercer.SecureHub;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class AuthManagerUnitTest {
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    UserDetailsService uds;
    @Autowired
    PasswordEncoder encoder;

    @Test
    void contextLoads() {
    }

    @Test
    void authenticateFailsForUnknownUser() {
        var ex = Assertions.assertThrows(AuthenticationException.class,
                () -> authManager.authenticate(new UsernamePasswordAuthenticationToken("nope", "pw")));
    }
}
