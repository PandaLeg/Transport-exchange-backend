package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PropertyRepo extends JpaRepository<Property, Long> {
    @Query(value = "SELECT * FROM properties p WHERE (p.ru_name = :name or p.en_name = :name or p.ua_name = :name)",
            nativeQuery = true)
    Property findByName(String name);

    @Query(value = "SELECT * FROM properties p WHERE (p.ru_name = :name or p.en_name = :name or p.ua_name = :name) and " +
            "p.property = :property", nativeQuery = true)
    Property findByNameAndProperty(String name, String property);
}
