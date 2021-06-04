package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.Confirmation;
import com.steshkovladyslav.transportexchangebackend.model.User;
import com.steshkovladyslav.transportexchangebackend.payload.request.users.PersonalData;
import com.steshkovladyslav.transportexchangebackend.repo.ConfirmationRepo;
import com.steshkovladyslav.transportexchangebackend.repo.LegalUserRepo;
import com.steshkovladyslav.transportexchangebackend.repo.UserRepo;
import com.steshkovladyslav.transportexchangebackend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${url.picture.path}")
    private String picturePath;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    GeneralComponent generalComponent;

    private final UserRepo userRepo;
    private final LegalUserRepo legalUserRepo;
    private final PasswordEncoder encoder;
    private final ConfirmationRepo confirmationRepo;

    @Autowired
    public UserService(UserRepo userRepo, LegalUserRepo legalUserRepo, PasswordEncoder encoder,
                       ConfirmationRepo confirmationRepo) {
        this.userRepo = userRepo;
        this.legalUserRepo = legalUserRepo;
        this.encoder = encoder;
        this.confirmationRepo = confirmationRepo;
    }

    public Map<String, Object> getUser(String jwtToken) {
        Map<String, Object> resultMap = new HashMap<>();

        String email = jwtUtils.getUserNameFromJwtToken(jwtToken);

        if (jwtUtils.validateJwtToken(jwtToken)) {
            User user = userRepo.findByEmail(email);

            Confirmation confirmation = confirmationRepo.findByUser_Id(user.getId());

            if (user != null) {
                resultMap.put("user", user);
                resultMap.put("status", confirmation != null);

                return resultMap;
            } else {
                return null;
            }
        }
        return null;
    }

    public ResponseEntity<?> editPersonalData(PersonalData personalData, MultipartFile photo, String role)
            throws IOException {
        String uuidFile = UUID.randomUUID().toString();

        User user = userRepo.findById((long) personalData.getId());

        if (user != null) {
            if (personalData.getFirstName() != null && !personalData.getFirstName().equals(user.getFirstName())) {
                user.setFirstName(personalData.getFirstName());
            }

            if (personalData.getLastName() != null && !personalData.getLastName().equals(user.getLastName())) {
                user.setLastName(personalData.getLastName());
            }

            if (personalData.getPatronymic() != null && !personalData.getPatronymic().equals(user.getPatronymic())) {
                user.setPatronymic(personalData.getPatronymic());
            }

            if (personalData.getFullName() != null && !personalData.getFullName().equals(user.getFullName())) {
                user.setFullName(personalData.getFullName());
            }

            if (personalData.getPhone() != null && !personalData.getPhone().equals(user.getPhone())) {
                user.setPhone(personalData.getPhone());
            }

            if (personalData.getEmail() != null && !personalData.getEmail().equals(user.getEmail())) {
                user.setEmail(personalData.getEmail());
            }

            if (photo != null && !photo.getOriginalFilename().isEmpty()) {
                generalComponent.setPhotoUser(photo, uuidFile, user);
            }

            userRepo.save(user);
            return ResponseEntity.ok("OK");
        }
        return null;
    }

    public ResponseEntity<?> editPassword(PersonalData personalData, String role) {
        User user = userRepo.findById((long) personalData.getId());

        if (personalData.getPassword() != null && !personalData.getPassword().equals(user.getPassword())) {
            user.setPassword(encoder.encode(personalData.getPassword()));
        }

        userRepo.save(user);

        return ResponseEntity.ok("OK");
    }

    public ResponseEntity<?> editBackgroundProfile(MultipartFile photoBackground, String role, String jwt) throws IOException {
        String email = jwtUtils.getUserNameFromJwtToken(jwt);
        String uuidFile = UUID.randomUUID().toString();

        if (jwtUtils.validateJwtToken(jwt)) {
            if (photoBackground != null && !photoBackground.getOriginalFilename().isEmpty()) {
                User user = userRepo.findByEmail(email);

                setBackgroundPhotoUser(photoBackground, uuidFile, user);

                userRepo.save(user);

                return ResponseEntity.ok("OK");
            }
        }
        return null;
    }


    private void setBackgroundPhotoUser(MultipartFile photoBackground, String uuidFile, User user)
            throws IOException {
        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        if (user.getProfileBackground() != null) {
            int i = user.getProfileBackground().lastIndexOf('/');
            String tempUrlPicture = user.getProfileBackground().substring(i);

            File file = new File(uploadPath + "/" + tempUrlPicture);

            if (file.delete()) {
                System.out.println("Успешно удален");
            } else {
                System.out.println("Ошибка, файл не был удалён");
            }
        }

        String resultFileName = uuidFile + "." + photoBackground.getOriginalFilename();
        photoBackground.transferTo(new File(uploadPath + "/" + resultFileName));

        user.setProfileBackground(picturePath + resultFileName);
    }

    public ResponseEntity<?> sendConfirmation(long id, Confirmation confirmation) {
        User user = userRepo.findById(id);

        if (user != null) {
            confirmation.setUser(user);
            confirmationRepo.save(confirmation);

            return ResponseEntity.ok("OK");
        }
        return null;
    }
}
