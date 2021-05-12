package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.*;
import com.steshkovladyslav.transportexchangebackend.repo.LegalUserRepo;
import com.steshkovladyslav.transportexchangebackend.repo.UserRepo;
import com.steshkovladyslav.transportexchangebackend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CargoTransportGeneral {
    @Autowired
    private JwtUtils jwtUtils;

    private final UserRepo userRepo;
    private final LegalUserRepo legalUserRepo;

    public CargoTransportGeneral(UserRepo userRepo, LegalUserRepo legalUserRepo) {
        this.userRepo = userRepo;
        this.legalUserRepo = legalUserRepo;
    }

    boolean setUserLegalUser(String token, Cargo cargo, Transport transport) {
        User user = null;
        LegalUser legalUser = null;

        if (jwtUtils.validateJwtToken(token)) {
            String email = jwtUtils.getUserNameFromJwtToken(token);

            if (email != null) {
                user = userRepo.findByEmail(email);
                legalUser = legalUserRepo.findByEmail(email);
            }

            if (user != null) {
                if (cargo != null) {
                    cargo.setUser(user);
                } else {
                    transport.setUser(user);
                }
            } else if (legalUser != null) {
                if (cargo != null) {
                    cargo.setLegalUser(legalUser);
                } else {
                    transport.setLegalUser(legalUser);
                }
            } else {
                return true;
            }
            return false;
        }
        return true;
    }
}

