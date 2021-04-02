package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.LegalUser;
import com.steshkovladyslav.transportexchangebackend.model.User;
import com.steshkovladyslav.transportexchangebackend.payload.request.users.PersonalData;
import com.steshkovladyslav.transportexchangebackend.payload.response.JwtLegalUserResponse;
import com.steshkovladyslav.transportexchangebackend.payload.response.JwtUserResponse;
import com.steshkovladyslav.transportexchangebackend.repo.LegalUserRepo;
import com.steshkovladyslav.transportexchangebackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${url.picture.path}")
    private String picturePath;

    private final UserRepo userRepo;
    private final LegalUserRepo legalUserRepo;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepo userRepo, LegalUserRepo legalUserRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.legalUserRepo = legalUserRepo;
        this.encoder = encoder;
    }

    public ResponseEntity<?> editPersonalData(PersonalData personalData, MultipartFile photo, String role, String jwt) throws IOException {
        List<String> roles;
        String uuidFile = UUID.randomUUID().toString();
        String resultFileName = "";

        if (role.equals("ROLE_USER")) {
            User user = userRepo.findById((long) personalData.getId());
            roles = user.getRoles().stream()
                    .map(item -> item.getName().name())
                    .collect(Collectors.toList());

            if (personalData.getFirstName() != null && !personalData.getFirstName().equals(user.getFirstName())) {
                user.setFirstName(personalData.getFirstName());
            }

            if (personalData.getLastName() != null && !personalData.getLastName().equals(user.getLastName())) {
                user.setLastName(personalData.getLastName());
            }

            if (personalData.getPatronymic() != null && !personalData.getPatronymic().equals(user.getPatronymic())) {
                user.setPatronymic(personalData.getPatronymic());
            }

            if (personalData.getPhone() != null && !personalData.getPhone().equals(user.getPhone())) {
                user.setPhone(personalData.getPhone());
            }

            if (personalData.getEmail() != null && !personalData.getEmail().equals(user.getEmail())) {
                user.setEmail(personalData.getEmail());
            }

            if (photo != null && !photo.getOriginalFilename().isEmpty()) {
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                if (user.getProfilePicture() != null) {
                    int i = user.getProfilePicture().lastIndexOf('/');
                    String tempUrlPicture = user.getProfilePicture().substring(i);

                    File file = new File(uploadPath + "/" + tempUrlPicture);

                    if (file.delete()) {
                        System.out.println("Успешно удален");
                    } else {
                        System.out.println("Ошибка, файл не был удалён");
                    }
                }

                resultFileName = uuidFile + "." + photo.getOriginalFilename();
                photo.transferTo(new File(uploadPath + "/" + resultFileName));

                user.setProfilePicture(picturePath + resultFileName);
            }

            userRepo.save(user);
            return ResponseEntity.ok(new JwtUserResponse(jwt,
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPatronymic(),
                    user.getCountry(),
                    user.getCity(),
                    user.getPhone(),
                    roles));
        } else {
            LegalUser legalUser = legalUserRepo.findById((long) personalData.getId());
            roles = legalUser.getRoles().stream()
                    .map(item -> item.getName().name())
                    .collect(Collectors.toList());

            if (personalData.getFirstName() != null && !personalData.getFirstName().equals(legalUser.getFirstName())) {
                legalUser.setFirstName(personalData.getFirstName());
            }

            if (personalData.getLastName() != null && !personalData.getLastName().equals(legalUser.getLastName())) {
                legalUser.setLastName(personalData.getLastName());
            }

            if (personalData.getPatronymic() != null && !personalData.getPatronymic().equals(legalUser.getPatronymic())) {
                legalUser.setPatronymic(personalData.getPatronymic());
            }

            if (personalData.getPhone() != null && !personalData.getPhone().equals(legalUser.getPhone())) {
                legalUser.setPhone(personalData.getPhone());
            }

            if (personalData.getEmail() != null && !personalData.getEmail().equals(legalUser.getEmail())) {
                legalUser.setEmail(personalData.getEmail());
            }

            legalUserRepo.save(legalUser);
            return ResponseEntity.ok(new JwtLegalUserResponse(jwt,
                    legalUser.getId(),
                    legalUser.getEmail(),
                    legalUser.getFirstName(),
                    legalUser.getLastName(),
                    legalUser.getPatronymic(),
                    legalUser.getCountry(),
                    legalUser.getCity(),
                    legalUser.getPhone(),
                    legalUser.getCompanyName(),
                    legalUser.getCompanyCode(),
                    roles));
        }
    }

    public ResponseEntity<?> editPassword(PersonalData personalData, String role) {
        if (role.equals("ROLE_USER")) {
            User user = userRepo.findById((long) personalData.getId());

            if (personalData.getPassword() != null && !personalData.getPassword().equals(user.getPassword())) {
                user.setPassword(encoder.encode(personalData.getPassword()));
            }

            userRepo.save(user);

            return ResponseEntity.ok("OK");
        } else {
            LegalUser legalUser = legalUserRepo.findById((long) personalData.getId());

            if (personalData.getPassword() != null && !personalData.getPassword().equals(legalUser.getPassword())) {
                legalUser.setPassword(encoder.encode(personalData.getPassword()));
            }

            legalUserRepo.save(legalUser);

            return ResponseEntity.ok("OK");
        }
    }
}
