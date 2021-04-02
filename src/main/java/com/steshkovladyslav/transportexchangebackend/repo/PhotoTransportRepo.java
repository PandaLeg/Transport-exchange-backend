package com.steshkovladyslav.transportexchangebackend.repo;

import com.steshkovladyslav.transportexchangebackend.model.PhotoTransport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoTransportRepo extends JpaRepository<PhotoTransport, Long> {
    List<PhotoTransport> findByTransport_Id(long id);
}
