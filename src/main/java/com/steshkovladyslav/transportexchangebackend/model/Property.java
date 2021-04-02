package com.steshkovladyslav.transportexchangebackend.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "properties")
@Data
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String ruName;
    private String enName;
    private String uaName;

    private String property;

    public Property() {
    }
}
