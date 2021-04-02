package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.PointLUCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PointLUCargoRepo extends JpaRepository<PointLUCargo, Long> {
    @Query(value = "SELECT * FROM point_lu_cargo plc WHERE plc.cargo_id in :ids", nativeQuery = true)
    List<PointLUCargo> findAllByIds(Iterable<Long> ids);

    @Query(value = "SELECT * FROM point_lu_cargo plc WHERE plc.cargo_id = :id", nativeQuery = true)
    List<PointLUCargo> getPointsLUCargoById(long id);
}
