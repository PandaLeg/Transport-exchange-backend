package com.steshkovladyslav.transportexchangebackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "point_lu_cargo")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
public class PointLUCargo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String cityFrom;
    private String countryFrom;
    private String cityTo;
    private String countryTo;

    private BigDecimal latFirstPoint;
    private BigDecimal lngFirstPoint;

    private BigDecimal latSecondPoint;
    private BigDecimal lngSecondPoint;

    @ManyToOne
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;

    public PointLUCargo() {
    }

    public PointLUCargo(String cityFrom, String countryFrom, String cityTo, String countryTo, BigDecimal latFirstPoint,
                        BigDecimal lngFirstPoint, BigDecimal latSecondPoint, BigDecimal lngSecondPoint) {
        this.cityFrom = cityFrom;
        this.countryFrom = countryFrom;
        this.cityTo = cityTo;
        this.countryTo = countryTo;
        this.latFirstPoint = latFirstPoint;
        this.lngFirstPoint = lngFirstPoint;
        this.latSecondPoint = latSecondPoint;
        this.lngSecondPoint = lngSecondPoint;
    }
}
