package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.ERole;
import com.steshkovladyslav.transportexchangebackend.model.Role;
import com.steshkovladyslav.transportexchangebackend.model.User;
import com.steshkovladyslav.transportexchangebackend.repo.RoleRepo;
import com.steshkovladyslav.transportexchangebackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;

    @Autowired
    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
    }

    public User addUser(User user, String type) {
        Set<Role> roles = new HashSet<>();
        Role userRole;

        if (type.equals("user")) {
            if (user.getFirstName() == null || user.getLastName() == null || user.getPatronymic() == null ||
                    user.getPhone() == null || user.getEmail() == null || user.getPassword() == null) {
                return null;
            }
            userRole = roleRepo.findByName(ERole.ROLE_USER);
        } else {
            if (user.getFullName() == null || user.getPhone() == null || user.getCompanyName() == null ||
                    user.getCompanyCode() == null || user.getEmail() == null || user.getPassword() == null) {
                return null;
            }
            userRole = roleRepo.findByName(ERole.ROLE_LEGAL_USER);
        }

        roles.add(userRole);

        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setLastVisit(LocalDateTime.now());

        return userRepo.save(user);
    }
}
