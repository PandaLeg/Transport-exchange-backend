package com.steshkovladyslav.transportexchangebackend.payload.request.transport;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TransportRequest {
    private String countryFrom;
    private String countryTo;

    private String cityFrom;
    private String cityTo;

    /* Габариты */
    private String carryingCapacityFrom;
    private String carryingCapacityUpTo;

    private String volumeFrom;
    private String volumeUpTo;

    private String bodyType;

    private String paymentForm;
    private String paymentTime;


    /* Дата загрузки */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate loadingDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate loadingDateBy;

    public TransportRequest() {
    }

    public TransportRequest(String countryFrom, String countryTo, String cityFrom, String cityTo,
                            String carryingCapacityFrom, String carryingCapacityUpTo, String volumeFrom,
                            String volumeUpTo, String bodyType, String paymentForm, String paymentTime,
                            LocalDate loadingDateFrom, LocalDate loadingDateBy) {
        this.countryFrom = countryFrom;
        this.countryTo = countryTo;
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
        this.carryingCapacityFrom = carryingCapacityFrom;
        this.carryingCapacityUpTo = carryingCapacityUpTo;
        this.volumeFrom = volumeFrom;
        this.volumeUpTo = volumeUpTo;
        this.bodyType = bodyType;
        this.paymentForm = paymentForm;
        this.paymentTime = paymentTime;
        this.loadingDateFrom = loadingDateFrom;
        this.loadingDateBy = loadingDateBy;
    }
}
