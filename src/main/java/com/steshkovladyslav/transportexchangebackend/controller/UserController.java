package com.steshkovladyslav.transportexchangebackend.controller;


import com.steshkovladyslav.transportexchangebackend.payload.request.users.PersonalData;
import com.steshkovladyslav.transportexchangebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/edit-personal-data", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public ResponseEntity<?> editPersonalData(
            @RequestPart("personalData") PersonalData personalData,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestParam("role") String role,
            @RequestParam("jwt") String jwt
    ) throws IOException {
        System.out.println(photo.getOriginalFilename());
        return userService.editPersonalData(personalData, photo, role, jwt);
    }

    @PutMapping("/edit-password")
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public ResponseEntity<?> editPassword(
            @RequestBody PersonalData personalData,
            @RequestParam("role") String role
    ) {
        return userService.editPassword(personalData, role);
    }
}
