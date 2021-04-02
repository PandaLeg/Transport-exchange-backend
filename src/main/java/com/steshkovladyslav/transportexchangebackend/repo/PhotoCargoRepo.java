package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.PhotoCargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoCargoRepo extends JpaRepository<PhotoCargo, Long> {
    List<PhotoCargo> findByCargo_Id(long id);
}
