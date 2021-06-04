package com.steshkovladyslav.transportexchangebackend.controller;

import com.steshkovladyslav.transportexchangebackend.model.*;
import com.steshkovladyslav.transportexchangebackend.payload.request.PropertiesRequest;
import com.steshkovladyslav.transportexchangebackend.payload.request.users.PersonalData;
import com.steshkovladyslav.transportexchangebackend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/statistics-of-cargo")
    public Map<String, Integer> statisticsOfCargo() {
        return adminService.calculateStatisticsOfCargo();
    }

    @GetMapping("/statistics-of-transports")
    public Map<String, Integer> statisticsOfTransport() {
        return adminService.calculateStatisticsOfTransport();
    }

    @GetMapping("/statistics-of-users")
    public Map<String, Integer> statisticsOfUsers() {
        return adminService.calculateStatisticsOfUsers();
    }

    @GetMapping("/statistics-count-of-cargo-transports")
    public Map<String, Object> statisticsCountOfCargoAndTransports() {
        return adminService.calculateStatisticsCountOfCargoAndTransports();
    }

    @GetMapping("/get-users")
    public Map<String, Object> getUsers() {
        return adminService.getUsers();
    }

    @GetMapping("/get-cargo-transports")
    public Map<String, Object> getCargoAndTransports() {
        return adminService.getCargoAndTransports();
    }

    @GetMapping("/get-user-from-admin-panel/{id}")
    public User getUserFromAdminPanel(
            @PathVariable("id") long id
    ) {
        return adminService.getUserFromAdminPanel(id);
    }

    @PostMapping(value = "/update-personal-data", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updatePersonalData(
            @RequestPart("personalData") PersonalData personalData,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws IOException {
        return adminService.updatePersonalData(personalData, photo);
    }

    @PostMapping(value = "/update-cargo", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCargo(
            @RequestParam String typeTransportation,
            @RequestPart("cargo") Cargo cargo,
            @RequestPart("placesCargo") List<PointLUCargo> placesCargo,
            @RequestPart("propertiesCargo") PropertiesRequest propertiesCargo,
            @RequestPart("imagesUrl") List<String> imagesUrl,
            @RequestPart(value = "firstPhoto", required = false) MultipartFile firstFile,
            @RequestPart(value = "secondPhoto", required = false) MultipartFile secondFile,
            @RequestPart(value = "thirdPhoto", required = false) MultipartFile thirdFile
    ) throws IOException {
        return adminService.updateCargo(typeTransportation, cargo, placesCargo, propertiesCargo, imagesUrl,
                firstFile, secondFile, thirdFile);
    }

    @PostMapping(value = "/update-transport", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateTransport(
            @RequestPart("transport") Transport transport,
            @RequestPart("placesTransport") List<PointLUTransport> placesTransport,
            @RequestPart("propertiesTransport") PropertiesRequest propertiesTransport,
            @RequestPart("imagesUrl") List<String> imagesUrl,
            @RequestPart(value = "firstPhoto", required = false) MultipartFile firstFile,
            @RequestPart(value = "secondPhoto", required = false) MultipartFile secondFile,
            @RequestPart(value = "thirdPhoto", required = false) MultipartFile thirdFile
    ) throws IOException {
        return adminService.updateTransport(transport, placesTransport, propertiesTransport, imagesUrl,
                firstFile, secondFile, thirdFile);
    }


    @GetMapping("/get-confirmations")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getConfirmation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int pageSize
    ) {
        return adminService.getConfirmations(page, pageSize);
    }

    @GetMapping("/get-confirmation/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Confirmation getConfirmation(
            @PathVariable("id") long id
    ) {
        return adminService.getConfirmation(id);
    }

    @PutMapping("/confirmation-company/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> confirmationCompany(
            @PathVariable("id") long id
    ) {
        return adminService.confirmationCompany(id);
    }
}
