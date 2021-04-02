package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"cargo", "transports"})
    User findById(long id);

    @EntityGraph(attributePaths = {"cargo", "transports"})
    User findByEmail(String email);
}
