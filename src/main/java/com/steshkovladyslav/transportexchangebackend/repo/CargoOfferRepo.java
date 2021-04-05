package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.CargoOffer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CargoOfferRepo extends JpaRepository<CargoOffer, Long> {
    @EntityGraph(attributePaths = {"cargo", "user", "legalUser"})
    List<CargoOffer> findAllByUser_Id(long id);

    @EntityGraph(attributePaths = {"cargo", "user", "legalUser"})
    List<CargoOffer> findAllByLegalUser_Id(long id);
}
