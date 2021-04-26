package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.repo.CargoRepo;
import com.steshkovladyslav.transportexchangebackend.repo.TransportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MainService {
    private final CargoRepo cargoRepo;
    private final TransportRepo transportRepo;

    @Autowired
    public MainService(CargoRepo cargoRepo, TransportRepo transportRepo) {
        this.cargoRepo = cargoRepo;
        this.transportRepo = transportRepo;
    }

    public Map<String, Object> getCountCargoAndTransports() {
        Map<String, Object> mapCount = new HashMap<>();
        long countCargo;
        long countTransports;

        countCargo = cargoRepo.findAll().size();
        countTransports = transportRepo.findAll().size();

        mapCount.put("countCargo", countCargo);
        mapCount.put("countTransports", countTransports);

        return mapCount;
    }
}
