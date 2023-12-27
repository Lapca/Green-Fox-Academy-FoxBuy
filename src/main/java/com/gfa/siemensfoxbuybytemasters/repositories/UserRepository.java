package com.gfa.siemensfoxbuybytemasters.repositories;

import com.gfa.siemensfoxbuybytemasters.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findUserByEmailVerificationToken(String token);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findUserByRoles(String role);
}


