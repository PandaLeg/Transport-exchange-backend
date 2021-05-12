package com.steshkovladyslav.transportexchangebackend.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PropertiesRequest implements Serializable {
    private List<String> typesLoadingTruck;
    private List<String> typesUnloadingTruck;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> permissions;
    private List<String> containerLoading;

    private String typePayment;
    private String costPer;

    private String paymentForm;
    private String paymentTime;

    public PropertiesRequest() {
    }

    public PropertiesRequest(List<String> typesLoadingTruck, List<String> typesUnloadingTruck,
                             List<String> permissions, List<String> containerLoading, String typePayment,
                             String costPer, String paymentForm, String paymentTime) {
        this.typesLoadingTruck = typesLoadingTruck;
        this.typesUnloadingTruck = typesUnloadingTruck;
        this.permissions = permissions;
        this.containerLoading = containerLoading;
        this.typePayment = typePayment;
        this.costPer = costPer;
        this.paymentForm = paymentForm;
        this.paymentTime = paymentTime;
    }
}
