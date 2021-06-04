package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.Confirmation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationRepo extends JpaRepository<Confirmation, Long> {
    @EntityGraph(attributePaths = {"user"})
    Page<Confirmation> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Confirmation findByUser_Id(long id);

    @EntityGraph(attributePaths = {"user"})
    Confirmation findById(long id);
}
