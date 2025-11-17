package com.academia.core.domain.auth;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    User save(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
