package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.TransportOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportOfferRepo extends JpaRepository<TransportOffer, Long> {
}
