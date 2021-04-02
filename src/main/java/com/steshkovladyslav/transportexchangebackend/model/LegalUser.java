package com.steshkovladyslav.transportexchangebackend.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "legal_users")
@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "email", "firstName", "lastName", "patronymic", "country", "phone", "companyName", "companyCode"})
public class LegalUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String password;
    private String email;

    private String firstName;
    private String lastName;
    private String patronymic;

    private String country;
    private String city;
    private String phone;

    private String companyName;
    private String companyCode;

    private String profilePicture;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "legal_user_roles",
            joinColumns = @JoinColumn(name = "legal_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "legalUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    private Set<Cargo> cargo = new HashSet<>();

    @OneToMany(mappedBy = "legalUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIdentityReference
    @JsonIdentityInfo(
            property = "id",
            generator = ObjectIdGenerators.PropertyGenerator.class
    )
    private Set<Transport> transports = new HashSet<>();

    public LegalUser() {
    }

    public LegalUser(String password, String email, String firstName, String lastName, String patronymic,
                     String country, String city, String phone, String companyName, String companyCode) {
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.country = country;
        this.city = city;
        this.phone = phone;
        this.companyName = companyName;
        this.companyCode = companyCode;
    }
}
