package com.cloudpiercer.SecureHub;

import com.cloudpiercer.SecureHub.model.AppUser;
import com.cloudpiercer.SecureHub.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SecureHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureHubApplication.class, args);
	}

	// Seed an initial admin and user for testing
	@Bean
	CommandLineRunner seedUsers(AppUserRepository repo, PasswordEncoder enc) {
		return args -> {
			if (repo.findByUsername("admin").isEmpty()) {
				AppUser admin = new AppUser();
				admin.setUsername("admin");
				admin.setPasswordHash(enc.encode("password123"));
				admin.setRole("ADMIN");
				admin.setActive(true);
				repo.save(admin);
			}
			if (repo.findByUsername("user").isEmpty()) {
				AppUser user = new AppUser();
				user.setUsername("user");
				user.setPasswordHash(enc.encode("password123"));
				user.setRole("USER");
				user.setActive(true);
				repo.save(user);
			}
		};
	}
}
