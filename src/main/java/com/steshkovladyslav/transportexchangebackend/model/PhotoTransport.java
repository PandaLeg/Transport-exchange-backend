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
@Table(name = "photo_transport")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
public class PhotoTransport implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String photoUrl;

    @ManyToOne
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    @JoinColumn(name = "transport_id")
    private Transport transport;

    public PhotoTransport() {
    }

    public PhotoTransport(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
