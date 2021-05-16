package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.User;
import com.steshkovladyslav.transportexchangebackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepo userRepo;

    @Autowired
    public UserDetailsServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);

        if (user != null) {
            return UserDetailsImpl.buildUser(user);
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

    }
}
