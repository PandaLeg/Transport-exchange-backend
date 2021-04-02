package com.steshkovladyslav.transportexchangebackend.payload.request.cargo;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CargoRequest {
    private String countryFrom;
    private String countryTo;

    private String cityFrom;
    private String cityTo;

    /* Габариты */
    private String weightFrom;
    private String weightUpTo;

    private String volumeFrom;
    private String volumeUpTo;

    private String nameCargo;
    private String bodyType;

    private String paymentForm;
    private String paymentTime;


    /* Дата загрузки */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate loadingDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    private LocalDate loadingDateBy;

    public CargoRequest() {
    }

    public CargoRequest(String countryFrom, String countryTo, String cityFrom, String cityTo, String weightFrom,
                        String weightUpTo, String volumeFrom, String volumeUpTo, String nameCargo, String bodyType,
                        String paymentForm, String paymentTime, LocalDate loadingDateFrom, LocalDate loadingDateBy) {
        this.countryFrom = countryFrom;
        this.countryTo = countryTo;
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
        this.weightFrom = weightFrom;
        this.weightUpTo = weightUpTo;
        this.volumeFrom = volumeFrom;
        this.volumeUpTo = volumeUpTo;
        this.nameCargo = nameCargo;
        this.bodyType = bodyType;
        this.paymentForm = paymentForm;
        this.paymentTime = paymentTime;
        this.loadingDateFrom = loadingDateFrom;
        this.loadingDateBy = loadingDateBy;
    }
}
