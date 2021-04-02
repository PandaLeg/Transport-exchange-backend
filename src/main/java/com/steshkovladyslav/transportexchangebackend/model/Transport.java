package com.steshkovladyslav.transportexchangebackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "transport")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
public class Transport implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Транспорт
    private String bodyType;

    private String carryingCapacityFrom;
    private String carryingCapacityUpTo;

    private String volumeFrom;
    private String volumeUpTo;

    private String lengthTransport;
    private String widthTransport;
    private String heightTransport;

    private String adr;

    /* Дата загрузки */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate loadingDateFrom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate loadingDateBy;

    /* Оплата */
    private String cost;
    // Валюта
    private String currency;
    // Предоплата
    private String prepayment;

    private String additional;

    @ManyToOne
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    @JoinColumn(name = "legal_user_id")
    private LegalUser legalUser;

    @OneToMany(mappedBy = "transport", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    private Set<PointLUTransport> pointsTransports = new HashSet<>();

    @OneToMany(mappedBy = "transport", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    private Set<PhotoTransport> photoTransport = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "transport_properties",
            joinColumns = @JoinColumn(name = "transport_id"),
            inverseJoinColumns = @JoinColumn(name = "property_id")
    )
    private Set<Property> propertiesTransport = new HashSet<>();

    public Transport() {
    }

    public Transport(String bodyType, String carryingCapacityFrom, String carryingCapacityUpTo, String volumeFrom,
                     String volumeUpTo, String lengthTransport, String widthTransport, String heightTransport,
                     String adr, LocalDate loadingDateFrom, LocalDate loadingDateBy, String cost, String currency,
                     String prepayment, String additional) {
        this.bodyType = bodyType;
        this.carryingCapacityFrom = carryingCapacityFrom;
        this.carryingCapacityUpTo = carryingCapacityUpTo;
        this.volumeFrom = volumeFrom;
        this.volumeUpTo = volumeUpTo;
        this.lengthTransport = lengthTransport;
        this.widthTransport = widthTransport;
        this.heightTransport = heightTransport;
        this.adr = adr;
        this.loadingDateFrom = loadingDateFrom;
        this.loadingDateBy = loadingDateBy;
        this.cost = cost;
        this.currency = currency;
        this.prepayment = prepayment;
        this.additional = additional;
    }
}
