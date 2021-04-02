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
@Table(name = "cargo")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
public class Cargo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    /* Габариты */
    private String weightFrom;
    private String weightUpTo;

    private String volumeFrom;
    private String volumeUpTo;

    private String lengthCargo;
    private String widthCargo;
    private String heightCargo;

    private String adr;

    /* Дата загрузки */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate loadingDateFrom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate loadingDateBy;

    /* Транспорт */
    private String bodyType;

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

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    private Set<PointLUCargo> pointsCargo = new HashSet<>();

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    private Set<PhotoCargo> photoCargo = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "cargo_properties",
            joinColumns = @JoinColumn(name = "cargo_id"),
            inverseJoinColumns = @JoinColumn(name = "property_id")
    )
    private Set<Property> propertiesCargo = new HashSet<>();

    public Cargo() {
    }

    public Cargo(String name, String weightFrom, String weightUpTo, String volumeFrom, String volumeUpTo,
                 String lengthCargo, String widthCargo, String heightCargo, String adr, LocalDate loadingDateFrom,
                 LocalDate loadingDateBy, String bodyType, String cost, String currency, String prepayment,
                 String additional) {
        this.name = name;
        this.weightFrom = weightFrom;
        this.weightUpTo = weightUpTo;
        this.volumeFrom = volumeFrom;
        this.volumeUpTo = volumeUpTo;
        this.lengthCargo = lengthCargo;
        this.widthCargo = widthCargo;
        this.heightCargo = heightCargo;
        this.adr = adr;
        this.loadingDateFrom = loadingDateFrom;
        this.loadingDateBy = loadingDateBy;
        this.bodyType = bodyType;
        this.cost = cost;
        this.currency = currency;
        this.prepayment = prepayment;
        this.additional = additional;
    }
}
