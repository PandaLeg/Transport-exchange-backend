package com.steshkovladyslav.transportexchangebackend.controller;

import com.steshkovladyslav.transportexchangebackend.model.ERole;
import com.steshkovladyslav.transportexchangebackend.model.LegalUser;
import com.steshkovladyslav.transportexchangebackend.model.Role;
import com.steshkovladyslav.transportexchangebackend.model.User;
import com.steshkovladyslav.transportexchangebackend.payload.request.LoginRequest;
import com.steshkovladyslav.transportexchangebackend.payload.response.JwtLegalUserResponse;
import com.steshkovladyslav.transportexchangebackend.payload.response.JwtUserResponse;
import com.steshkovladyslav.transportexchangebackend.repo.RoleRepo;
import com.steshkovladyslav.transportexchangebackend.security.jwt.JwtUtils;
import com.steshkovladyslav.transportexchangebackend.service.AuthService;
import com.steshkovladyslav.transportexchangebackend.service.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepo roleRepo;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    public AuthorizationController(AuthService authService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder,
                                   AuthenticationManager authenticationManager, RoleRepo roleRepo) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleRepo = roleRepo;
    }

    @PostMapping("/sign-in-user")
    public ResponseEntity<?> authenticationUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        logger.info("STEP AUTHENTICATION MANAGER");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("STEP SET AUTHENTICATION");
        String jwt = jwtUtils.generateJwtToken(authentication);
        logger.info("STEP GENERATE TOKEN");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("STEP GET PRINCIPAL");
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        if (userDetails.getCompanyName() != null && !userDetails.getCompanyName().equals("")) {
            return ResponseEntity.ok(new JwtLegalUserResponse(jwt,
                    userDetails.getId(),
                    userDetails.getEmail(),
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    userDetails.getPatronymic(),
                    userDetails.getCountry(),
                    userDetails.getCity(),
                    userDetails.getPhone(),
                    userDetails.getCompanyName(),
                    userDetails.getCompanyCode(),
                    roles));
        } else {
            return ResponseEntity.ok(new JwtUserResponse(jwt,
                    userDetails.getId(),
                    userDetails.getEmail(),
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    userDetails.getPatronymic(),
                    userDetails.getCountry(),
                    userDetails.getCity(),
                    userDetails.getPhone(),
                    roles));
        }
    }

    @PostMapping("/sign-up-user")
    public User addUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepo.findByName(ERole.ROLE_USER);
        Role adminRole = roleRepo.findByName(ERole.ROLE_ADMIN);

        roles.add(userRole);
        roles.add(adminRole);

        user.setRoles(roles);

        return authService.addUser(user);
    }

    @PostMapping("/sign-up-legal-user")
    public LegalUser addLegalUser(@RequestBody LegalUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();

        Role adminRole = roleRepo.findByName(ERole.ROLE_ADMIN);
        Role userRole = roleRepo.findByName(ERole.ROLE_LEGAL_USER);
        roles.add(userRole);

        user.setRoles(roles);

        return authService.addLegalUser(user);
    }

    @PostMapping("/validate-user")
    public ResponseEntity<?> validationUser(@RequestParam(required = false) String token) {
        if (jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.ok(0);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
