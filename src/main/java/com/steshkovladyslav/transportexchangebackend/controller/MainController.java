package com.steshkovladyslav.transportexchangebackend.controller;


import com.steshkovladyslav.transportexchangebackend.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class MainController {
    private final MainService mainService;

    @Autowired
    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/get-count-cargo-transports")
    public Map<String, Object> getCountCargoAndTransports(){
        return mainService.getCountCargoAndTransports();
    }
}
