package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.PointLUTransport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PointLUTransRepo extends JpaRepository<PointLUTransport, Long> {
    @Query(value = "SELECT * FROM point_lu_transport plt WHERE plt.transport_id in :ids", nativeQuery = true)
    List<PointLUTransport> findAllByIds(Iterable<Long> ids);

    @Query(value = "SELECT * FROM point_lu_transport plt WHERE plt.transport_id = :id", nativeQuery = true)
    List<PointLUTransport> getPointsLUTransportById(long id);

    @Query(value = "SELECT * FROM point_lu_transport plt WHERE plt.country_from = CAST (:country AS varchar) OR " +
            "plt.country_to = CAST (:country AS varchar)", nativeQuery = true)
    List<PointLUTransport> getPointsByCountryFromOrCountryTo(String country);
}
