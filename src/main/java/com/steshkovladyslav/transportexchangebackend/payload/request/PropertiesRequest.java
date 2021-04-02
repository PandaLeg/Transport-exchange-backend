package com.steshkovladyslav.transportexchangebackend.payload.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PropertiesRequest implements Serializable{
    private List<String> typesLoadingTruck;
    private List<String> typesUnloadingTruck;

    private List<String> permissions;

    private String typePayment;
    private String costPer;

    private String paymentForm;
    private String paymentTime;

    public PropertiesRequest() {
    }

    public PropertiesRequest(List<String> typesLoadingTruck, List<String> typesUnloadingTruck,
                             List<String> permissions, String typePayment, String costPer, String paymentForm,
                             String paymentTime) {
        this.typesLoadingTruck = typesLoadingTruck;
        this.typesUnloadingTruck = typesUnloadingTruck;
        this.permissions = permissions;
        this.typePayment = typePayment;
        this.costPer = costPer;
        this.paymentForm = paymentForm;
        this.paymentTime = paymentTime;
    }
}
