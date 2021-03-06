package com.steshkovladyslav.transportexchangebackend.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.steshkovladyslav.transportexchangebackend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String firstName;
    private String lastName;
    private String patronymic;

    private String fullName;

    private String email;

    private String country;
    private String city;
    private String phone;

    private String companyName;
    private String companyCode;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String firstName, String lastName, String patronymic, String fullName, String email,
                           String country, String city, String phone, String companyName, String companyCode,
                           String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.fullName = fullName;
        this.email = email;
        this.country = country;
        this.city = city;
        this.phone = phone;
        this.companyName = companyName;
        this.companyCode = companyCode;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl buildUser(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getFullName(),
                user.getEmail(),
                user.getCountry(),
                user.getCity(),
                user.getPhone(),
                user.getCompanyName(),
                user.getCompanyCode(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
