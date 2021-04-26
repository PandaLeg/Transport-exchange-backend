package com.steshkovladyslav.transportexchangebackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transport_offer")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
public class TransportOffer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String additional;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    @JoinColumn(name = "transport_id")
    private Transport transport;

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

    public TransportOffer() {
    }

    public TransportOffer(String additional) {
        this.additional = additional;
    }
}
