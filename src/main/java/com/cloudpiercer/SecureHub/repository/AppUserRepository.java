package com.cloudpiercer.SecureHub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloudpiercer.SecureHub.model.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
