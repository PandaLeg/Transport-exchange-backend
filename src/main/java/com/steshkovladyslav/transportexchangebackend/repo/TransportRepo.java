package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.Transport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TransportRepo extends JpaRepository<Transport, Long> {
    @EntityGraph(attributePaths = {"user", "legalUser", "photoTransport", "propertiesTransport", "pointsTransports"})
    Transport findById(long id);

    @EntityGraph(attributePaths = {"photoTransport", "propertiesTransport", "pointsTransports"})
    List<Transport> findByUser_Id(long id);

    @EntityGraph(attributePaths = {"photoTransport", "propertiesTransport", "pointsTransports"})
    List<Transport> findByLegalUser_Id(long id);

    @Query(value = "SELECT transport_id " +
            "FROM point_lu_transport plt where (:countryFrom is null or plt.country_from = " +
            "CAST (:countryFrom as varchar)) and (:cityFrom is null or plt.city_from = " +
            "CAST (:cityFrom as varchar)) and (:countryTo is null or plt.country_to = " +
            "CAST (:countryTo as varchar)) and (:cityTo is null or plt.city_to = " +
            "CAST (:cityTo as varchar))", nativeQuery = true)
    Set<Long> getTransportIds(String countryFrom, String cityFrom, String countryTo, String cityTo);

    @Query(value = "SELECT * FROM transport t WHERE (t.id in :ids) and (:carryingCapacityFrom is null or " +
            "t.carrying_capacity_from = " +
            "CAST (:carryingCapacityFrom as varchar)) and (:carryingCapacityUpTo is null or t.carrying_capacity_up_to = " +
            "CAST (:carryingCapacityUpTo as varchar)) and (:volumeFrom is null or t.volume_from = " +
            "CAST (:volumeFrom as varchar)) and (:volumeUpTo is null or t.volume_up_to = " +
            "CAST (:volumeUpTo as varchar)) and (:bodyType is null or t.body_type = CAST (:bodyType as varchar))",
            nativeQuery = true)
    Page<Transport> searchTransportsWithParams(Iterable<Long> ids, String carryingCapacityFrom, String carryingCapacityUpTo,
                                               String volumeFrom, String volumeUpTo, String bodyType, Pageable pageable);


    @Query(value = "SELECT * FROM transport t WHERE (t.id in :ids) and (t.loading_date_from >= :loadingDateFrom) and " +
            "(:carryingCapacityFrom is null or t.carrying_capacity_from = CAST (:carryingCapacityFrom as varchar)) and " +
            "(:carryingCapacityUpTo is null or t.carrying_capacity_up_to = CAST (:carryingCapacityUpTo as varchar)) and  " +
            "(:volumeFrom is null or t.volume_from = CAST (:volumeFrom as varchar)) and " +
            "(:volumeUpTo is null or t.volume_up_to = CAST (:volumeUpTo as varchar)) and (:bodyType is null or " +
            "t.body_type = CAST (:bodyType as varchar))", nativeQuery = true)
    Page<Transport> findByLoadingDateFrom(Iterable<Long> ids, LocalDate loadingDateFrom, String carryingCapacityFrom,
                                          String carryingCapacityUpTo, String volumeFrom, String volumeUpTo,
                                          String bodyType, Pageable pageable);

    @Query(value = "SELECT * FROM transport t WHERE (t.id in :ids) and (t.loading_date_by = :loadingDateBy) and " +
            "(:carryingCapacityFrom is null or t.carrying_capacity_from = CAST (:carryingCapacityFrom as varchar)) and " +
            "(:carryingCapacityUpTo is null or t.carrying_capacity_up_to = CAST (:carryingCapacityUpTo as varchar)) and  " +
            "(:volumeFrom is null or t.volume_from = CAST (:volumeFrom as varchar)) and " +
            "(:volumeUpTo is null or t.volume_up_to = CAST (:volumeUpTo as varchar)) and (:bodyType is null or " +
            "t.body_type = CAST (:bodyType as varchar))", nativeQuery = true)
    Page<Transport> findByLoadingDateBy(Iterable<Long> ids, LocalDate loadingDateBy, String carryingCapacityFrom,
                                        String carryingCapacityUpTo, String volumeFrom, String volumeUpTo,
                                        String bodyType, Pageable pageable);

    @Query(value = "SELECT * FROM transport t WHERE (t.id in :ids) and " +
            "(t.loading_date_from >= :loadingDateFrom) and (t.loading_date_by <= :loadingDateBy) and " +
            "(:carryingCapacityFrom is null or t.carrying_capacity_from = CAST (:carryingCapacityFrom as varchar)) and " +
            "(:carryingCapacityUpTo is null or t.carrying_capacity_up_to = CAST (:carryingCapacityUpTo as varchar)) and  " +
            "(:volumeFrom is null or t.volume_from = CAST (:volumeFrom as varchar)) and " +
            "(:volumeUpTo is null or t.volume_up_to = CAST (:volumeUpTo as varchar)) and (:bodyType is null or " +
            "t.body_type = CAST (:bodyType as varchar))", nativeQuery = true)
    Page<Transport> getAllBetweenTwoDate(Iterable<Long> ids, LocalDate loadingDateFrom, LocalDate loadingDateBy,
                                         String carryingCapacityFrom, String carryingCapacityUpTo, String volumeFrom,
                                         String volumeUpTo, String bodyType, Pageable pageable);
}
