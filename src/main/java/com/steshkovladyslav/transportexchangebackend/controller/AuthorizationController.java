package com.steshkovladyslav.transportexchangebackend.controller;

import com.steshkovladyslav.transportexchangebackend.model.User;
import com.steshkovladyslav.transportexchangebackend.payload.request.LoginRequest;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    public AuthorizationController(AuthService authService, JwtUtils jwtUtils,
                                   AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
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

        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/sign-up-user")
    public User addUser(
            @RequestBody User user,
            @RequestParam("type") String type
    ) {
        return authService.addUser(user, type);
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
