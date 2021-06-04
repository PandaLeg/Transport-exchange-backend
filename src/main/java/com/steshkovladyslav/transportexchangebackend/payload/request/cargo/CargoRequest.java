package com.steshkovladyslav.transportexchangebackend.payload.request.cargo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
public class CargoRequest implements Serializable {
    private String countryFrom;
    private String countryTo;

    private String cityFrom;
    private String cityTo;

    /* Габариты */
    private String weightFrom;
    private String weightUpTo;

    private String volumeFrom;
    private String volumeUpTo;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> typesTransportation;
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
                        String weightUpTo, String volumeFrom, String volumeUpTo, List<String> typesTransportation,
                        String nameCargo, String bodyType, String paymentForm, String paymentTime,
                        LocalDate loadingDateFrom, LocalDate loadingDateBy) {
        this.countryFrom = countryFrom;
        this.countryTo = countryTo;
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
        this.weightFrom = weightFrom;
        this.weightUpTo = weightUpTo;
        this.volumeFrom = volumeFrom;
        this.volumeUpTo = volumeUpTo;
        this.typesTransportation = typesTransportation;
        this.nameCargo = nameCargo;
        this.bodyType = bodyType;
        this.paymentForm = paymentForm;
        this.paymentTime = paymentTime;
        this.loadingDateFrom = loadingDateFrom;
        this.loadingDateBy = loadingDateBy;
    }
}
