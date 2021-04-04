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

    @GetMapping("/get-user")
    public <T> T getUser(
            @RequestParam("jwtToken") String jwtToken
    ) {
        return userService.getUser(jwtToken);
    }

    @PostMapping(value = "/edit-personal-data", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public ResponseEntity<?> editPersonalData(
            @RequestPart("personalData") PersonalData personalData,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @RequestParam("role") String role
    ) throws IOException {
        if (photo != null) {
            System.out.println(photo.getOriginalFilename());
        }
        return userService.editPersonalData(personalData, photo, role);
    }

    @PostMapping(value = "/edit-background-profile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('LEGAL_USER') or hasRole('ADMIN')")
    public ResponseEntity<?> editBackgroundProfile(
            @RequestPart(value = "photoBackground", required = false) MultipartFile photoBackground,
            @RequestParam("role") String role,
            @RequestParam("jwt") String jwt
    ) throws IOException {
        if (photoBackground != null) {
            System.out.println(photoBackground.getOriginalFilename());
        }
        return userService.editBackgroundProfile(photoBackground, role, jwt);
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
