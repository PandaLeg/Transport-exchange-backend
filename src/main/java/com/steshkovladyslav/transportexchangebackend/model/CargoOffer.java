package com.steshkovladyslav.transportexchangebackend.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cargo_offer")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
public class CargoOffer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


}
