package com.steshkovladyslav.transportexchangebackend.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "types")
@Data
public class Type implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String ruName;
    private String enName;
    private String uaName;

    private String type;

    public Type() {
    }
}
