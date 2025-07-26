package rw.ac.auca.ecommerce.core.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.ac.auca.ecommerce.core.user.model.User;

import java.util.Optional;
import java.util.UUID;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}

