package com.steshkovladyslav.transportexchangebackend.controller;

import com.steshkovladyslav.transportexchangebackend.model.Cargo;
import com.steshkovladyslav.transportexchangebackend.model.CargoOffer;
import com.steshkovladyslav.transportexchangebackend.model.PhotoCargo;
import com.steshkovladyslav.transportexchangebackend.model.PointLUCargo;
import com.steshkovladyslav.transportexchangebackend.payload.request.PropertiesRequest;
import com.steshkovladyslav.transportexchangebackend.payload.request.cargo.CargoRequest;
import com.steshkovladyslav.transportexchangebackend.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/cargo")
public class CargoController {
    private final CargoService cargoService;

    @Autowired
    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping(value = "/get-cargo")
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public List<Cargo> getAllCargo() {
        return cargoService.getAllCargo();
    }

    @GetMapping("get-count-cargo/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public Integer getCountCargo(
            @PathVariable("id") long id,
            @RequestParam String role
    ) {
        return cargoService.getCountCargo(id, role);
    }

    @PostMapping(value = "/add-cargo", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public Cargo addCargo(
            @RequestParam String token,
            @RequestPart("cargo") Cargo cargo,
            @RequestPart("placesCargo") List<PointLUCargo> placesCargo,
            @RequestPart("propertiesCargo") PropertiesRequest propertiesCargo,
            @RequestParam("lang") String lang,
            @RequestPart(value = "firstPhoto", required = false) MultipartFile firstFile,
            @RequestPart(value = "secondPhoto", required = false) MultipartFile secondFile,
            @RequestPart(value = "thirdPhoto", required = false) MultipartFile thirdFile
    ) {
        return cargoService.addCargo(token, cargo, propertiesCargo, placesCargo, lang, firstFile, secondFile,
                thirdFile);
    }

    @PostMapping("/search-cargo")
    public Map<String, Object> searchCargo(
            @RequestBody CargoRequest cargoRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int pageSize
    ) {
        return cargoService.searchCargo(cargoRequest, page, pageSize);
    }

    @GetMapping("/get-cargo/{id}")
    public Map<String, Object> getCargo(@PathVariable long id) {
        return cargoService.getCargo(id);
    }

    @GetMapping("/get-points-cargo")
    public List<PointLUCargo> getPointsCargo(
            @RequestParam("id") long id
    ) {
        return cargoService.getPointsCargo(id);
    }

    @GetMapping("/get-photos-cargo/{id}")
    public List<PhotoCargo> getPhotoCargo(
            @PathVariable("id") long id
    ) {
        return cargoService.getPhotoCargo(id);
    }

    @GetMapping("/get-all-offer-cargo/{id}")
    public Map<String, Object> getAllOfferCargo(
            @PathVariable("id") long id,
            @RequestParam("role") String role
    ) {
        return cargoService.getAllOfferCargo(id, role);
    }

    @GetMapping("/get-active-offers-cargo/{id}")
    public Map<String, Object> getActiveOffersCargo(
            @PathVariable("id") long id,
            @RequestParam("role") String role
    ) {
        return cargoService.getActiveOffersCargo(id, role);
    }

    @PostMapping("/send-cargo-offer/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addCargoOffer(
            @PathVariable("id") long idCargo,
            @RequestBody CargoOffer cargoOffer,
            @RequestParam("role") String role,
            @RequestParam("idUser") long idUser
    ) {
        return cargoService.addCargoOffer(idCargo, cargoOffer, role, idUser);
    }

    @GetMapping("/get-sent-offers-cargo/{id}")
    public List<Cargo> getSentOffersCargo(
            @PathVariable("id") long id,
            @RequestParam("role") String role
    ){
        return cargoService.getSentOffersCargo(id, role);
    }
}
