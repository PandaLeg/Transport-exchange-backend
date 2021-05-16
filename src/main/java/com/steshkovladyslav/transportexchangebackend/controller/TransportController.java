package com.steshkovladyslav.transportexchangebackend.controller;

import com.steshkovladyslav.transportexchangebackend.model.*;
import com.steshkovladyslav.transportexchangebackend.payload.request.PropertiesRequest;
import com.steshkovladyslav.transportexchangebackend.payload.request.transport.TransportRequest;
import com.steshkovladyslav.transportexchangebackend.service.TransportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/transport")
public class TransportController {
    private final TransportService transportService;

    @Autowired
    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping("/get-transports")
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public List<Transport> getTransports() {
        return transportService.getTransports();
    }

    @GetMapping("get-count-transports/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public Integer getCountTransport(
            @PathVariable("id") long id,
            @RequestParam String role
    ) {
        return transportService.getCountTransport(id, role);
    }

    @PostMapping("/add-transport")
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public Transport addTransport(
            @RequestParam String token,
            @RequestPart("transport") Transport transport,
            @RequestPart("placesTransport") List<PointLUTransport> placesTransport,
            @RequestPart("propertiesTransport") PropertiesRequest propertiesTransport,
            @RequestPart(value = "firstPhoto", required = false) MultipartFile firstFile,
            @RequestPart(value = "secondPhoto", required = false) MultipartFile secondFile,
            @RequestPart(value = "thirdPhoto", required = false) MultipartFile thirdFile
    ) {
        return transportService.addTransport(token, transport, placesTransport, propertiesTransport,
                firstFile, secondFile, thirdFile);
    }

    @PostMapping("/search-transport")
    public Map<String, Object> searchTransport(
            @RequestBody TransportRequest transportRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int pageSize
    ) {
        return transportService.searchTransport(transportRequest, page, pageSize);
    }

    @GetMapping("/get-transport/{id}")
    public Map<String, Object> getTransport(
            @PathVariable("id") long id
    ) {
        return transportService.getTransport(id);
    }

    @GetMapping("/get-points-transport")
    public List<PointLUTransport> getPointsTransport(
            @RequestParam("id") long id
    ) {
        return transportService.getPointsTransport(id);
    }

    @GetMapping("/get-photos-transport/{id}")
    public List<PhotoTransport> getPhotoTransport(
            @PathVariable("id") long id
    ) {
        return transportService.getPhotoTransport(id);
    }

    @PostMapping("/send-transport-offer/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public ResponseEntity<?> addTransportOffer(
            @PathVariable("id") long idTransport,
            @RequestBody TransportOffer transportOffer,
            @RequestParam("role") String role,
            @RequestParam("idUser") long idUser
    ) {
        return transportService.addTransportOffer(idTransport, transportOffer, role, idUser);
    }

    @GetMapping("/get-all-offer-transports/{id}")
    public Map<String, Object> getAllOfferTransports(
            @PathVariable("id") long id,
            @RequestParam("role") String role
    ) {
        return transportService.getAllOfferTransports(id, role);
    }

    @GetMapping("/get-active-sent-offers-transports/{id}")
    public Map<String, Object> getActiveAndSentOffersTransports(
            @PathVariable("id") long id,
            @RequestParam("role") String role
    ) {
        return transportService.getActiveAndSentOffersTransports(id, role);
    }

    @GetMapping("/get-sent-offers-transports/{id}")
    public List<Transport> getSentOffersTransports(
            @PathVariable("id") long id,
            @RequestParam("role") String role
    ){
        return transportService.getSentOffersTransports(id, role);
    }

    @PutMapping("/change-status-transport-offer")
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public Transport changeStatusAndAddToProcessing(
            @RequestParam("id") Long id
    ){
        return transportService.changeStatusTransport(id);
    }

    @PostMapping(value = "/get-count-places", consumes = {"multipart/form-data"})
    public Map<String, Object> getCountPlaces(
            @RequestPart("countries") List<String> countries
    ) {
        return transportService.getCountPlaces(countries);
    }
}
