package com.steshkovladyslav.transportexchangebackend.payload.request.users;

import lombok.Data;

@Data
public class PersonalData {
    private Long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String fullName;
    private String phone;
    private String email;
    private String companyName;
    private String companyCode;

    private String password;

    private String profilePicture;

    public PersonalData() {
    }

    public PersonalData(Long id, String firstName, String lastName, String patronymic, String fullName, String phone,
                        String email, String companyName, String companyCode, String password, String profilePicture) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.companyName = companyName;
        this.companyCode = companyCode;
        this.password = password;
        this.profilePicture = profilePicture;
    }
}
