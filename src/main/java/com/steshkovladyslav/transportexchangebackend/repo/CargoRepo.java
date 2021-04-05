package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.Cargo;
import com.steshkovladyslav.transportexchangebackend.model.PointLUCargo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CargoRepo extends JpaRepository<Cargo, Long> {
    @EntityGraph(attributePaths = {"photoCargo", "propertiesCargo", "pointsCargo"})
    List<Cargo> findAll();

    @EntityGraph(attributePaths = {"user", "legalUser", "photoCargo", "propertiesCargo", "pointsCargo"})
    Cargo findById(long id);

    @EntityGraph(attributePaths = {"user", "legalUser", "photoCargo", "propertiesCargo", "pointsCargo"})
    List<Cargo> findAllByUser_Id(long id);

    @EntityGraph(attributePaths = {"user", "legalUser", "photoCargo", "propertiesCargo", "pointsCargo"})
    List<Cargo> findAllByLegalUser_Id(long id);

    @Query(value = "SELECT cargo_id " +
            "FROM point_lu_cargo plc where (:countryFrom is null or plc.country_from = " +
            "CAST (:countryFrom as varchar)) and (:cityFrom is null or plc.city_from = " +
            "CAST (:cityFrom as varchar)) and (:countryTo is null or plc.country_to = " +
            "CAST (:countryTo as varchar)) and (:cityTo is null or plc.city_to = " +
            "CAST (:cityTo as varchar))", nativeQuery = true)
    Set<Long> getCargoIds(String countryFrom, String cityFrom, String countryTo, String cityTo);

    /*@Query(value = "SELECT * FROM cargo c WHERE (:countryFrom is null or c.country_first_loading_point = " +
            "CAST (:countryFrom as varchar)) and (:cityFrom is null or c.city_first_loading_point = " +
            "CAST (:cityFrom as varchar)) and  (:countryTo is null or c.country_first_unloading_point = " +
            "CAST (:countryTo as varchar)) and (:cityTo is null or c.city_first_unloading_point = " +
            "CAST (:cityTo as varchar)) and (:weightFrom is null or c.weight_from = CAST (:weightFrom as varchar)) " +
            "and (:weightUpTo is null or c.weight_up_to = CAST (:weightUpTo as varchar)) and (:volumeFrom is null or " +
            "c.volume_from = CAST (:volumeFrom as varchar)) and (:volumeUpTo is null or c.volume_up_to = " +
            "CAST (:volumeUpTo as varchar)) and (:nameCargo is null or c.name = CAST (:nameCargo as varchar)) and " +
            "(:bodyType is null or c.body_type = CAST (:bodyType as varchar))", nativeQuery = true)*/
    @Query(value = "SELECT * FROM cargo c WHERE (c.id in :ids) and (:weightFrom is null or " +
            "c.weight_from = CAST (:weightFrom as varchar)) and (:weightUpTo is null or " +
            "c.weight_up_to = CAST (:weightUpTo as varchar)) and (:volumeFrom is null or " +
            "c.volume_from = CAST (:volumeFrom as varchar)) and (:volumeUpTo is null or c.volume_up_to = " +
            "CAST (:volumeUpTo as varchar)) and (:nameCargo is null or c.name = CAST (:nameCargo as varchar)) and " +
            "(:bodyType is null or c.body_type = CAST (:bodyType as varchar))", nativeQuery = true)
    Page<Cargo> searchCargoWithParams(Iterable<Long> ids, String weightFrom, String weightUpTo, String volumeFrom,
                                      String volumeUpTo, String nameCargo, String bodyType, Pageable pageable);


    @Query(value = "SELECT * FROM cargo c WHERE (c.id in :ids) and (c.loading_date_from >= :loadingDateFrom) and " +
            "(:weightFrom is null or c.weight_from = CAST (:weightFrom as varchar)) " +
            "and (:weightUpTo is null or c.weight_up_to = CAST (:weightUpTo as varchar)) and (:volumeFrom is null or " +
            "c.volume_from = CAST (:volumeFrom as varchar)) and (:volumeUpTo is null or c.volume_up_to = " +
            "CAST (:volumeUpTo as varchar)) and (:nameCargo is null or c.name = CAST (:nameCargo as varchar)) and " +
            "(:bodyType is null or c.body_type = CAST (:bodyType as varchar))", nativeQuery = true)
    Page<Cargo> findByLoadingDateFrom(Iterable<Long> ids, LocalDate loadingDateFrom, String weightFrom,
                                      String weightUpTo, String volumeFrom, String volumeUpTo, String nameCargo,
                                      String bodyType, Pageable pageable);

    @Query(value = "SELECT * FROM cargo c WHERE (c.id in :ids) and (c.loading_date_by = :loadingDateBy) and " +
            "(:weightFrom is null or c.weight_from = CAST (:weightFrom as varchar)) " +
            "and (:weightUpTo is null or c.weight_up_to = CAST (:weightUpTo as varchar)) and (:volumeFrom is null or " +
            "c.volume_from = CAST (:volumeFrom as varchar)) and (:volumeUpTo is null or c.volume_up_to = " +
            "CAST (:volumeUpTo as varchar)) and (:nameCargo is null or c.name = CAST (:nameCargo as varchar)) and " +
            "(:bodyType is null or c.body_type = CAST (:bodyType as varchar))", nativeQuery = true)
    Page<Cargo> findByLoadingDateBy(Iterable<Long> ids, LocalDate loadingDateBy, String weightFrom, String weightUpTo,
                                    String volumeFrom, String volumeUpTo, String nameCargo, String bodyType,
                                    Pageable pageable);

    @Query(value = "SELECT * FROM cargo c WHERE (c.id in :ids) and " +
            "(c.loading_date_from >= :loadingDateFrom) and (c.loading_date_by <= :loadingDateBy) and " +
            "(:weightFrom is null or c.weight_from = CAST (:weightFrom as varchar)) and " +
            "(:weightUpTo is null or c.weight_up_to = CAST (:weightUpTo as varchar)) and (:volumeFrom is null or " +
            "c.volume_from = CAST (:volumeFrom as varchar)) and (:volumeUpTo is null or c.volume_up_to = " +
            "CAST (:volumeUpTo as varchar)) and (:nameCargo is null or c.name = CAST (:nameCargo as varchar)) and " +
            "(:bodyType is null or c.body_type = CAST (:bodyType as varchar))", nativeQuery = true)
    Page<Cargo> getAllBetweenTwoDate(Iterable<Long> ids, LocalDate loadingDateFrom, LocalDate loadingDateBy,
                                     String weightFrom, String weightUpTo, String volumeFrom,
                                     String volumeUpTo, String nameCargo, String bodyType, Pageable pageable);


    @Query(value = "select * from cargo inner join cargo_offer on cargo.id = cargo_offer.cargo_id", nativeQuery = true)
    List<Cargo> getByCargoId();
}
