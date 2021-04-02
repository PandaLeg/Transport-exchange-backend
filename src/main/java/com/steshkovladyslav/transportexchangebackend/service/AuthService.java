package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.LegalUser;
import com.steshkovladyslav.transportexchangebackend.model.User;
import com.steshkovladyslav.transportexchangebackend.repo.LegalUserRepo;
import com.steshkovladyslav.transportexchangebackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepo userRepo;
    private final LegalUserRepo legalUserRepo;

    @Autowired
    public AuthService(UserRepo userRepo, LegalUserRepo legalUserRepo) {
        this.userRepo = userRepo;
        this.legalUserRepo = legalUserRepo;
    }

    public User addUser(User user) {
        return userRepo.save(user);
    }

    public LegalUser addLegalUser(LegalUser user) {
        return legalUserRepo.save(user);
    }
}
