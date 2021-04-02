package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.LegalUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalUserRepo extends JpaRepository<LegalUser, Long> {
    @EntityGraph(attributePaths = {"cargo", "transports"})
    LegalUser findById(long id);

    @EntityGraph(attributePaths = {"cargo", "transports"})
    LegalUser findByEmail(String email);
}
