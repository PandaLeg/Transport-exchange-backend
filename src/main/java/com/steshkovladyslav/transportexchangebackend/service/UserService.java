package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.LegalUser;
import com.steshkovladyslav.transportexchangebackend.model.User;
import com.steshkovladyslav.transportexchangebackend.payload.request.users.PersonalData;
import com.steshkovladyslav.transportexchangebackend.payload.response.JwtLegalUserResponse;
import com.steshkovladyslav.transportexchangebackend.payload.response.JwtUserResponse;
import com.steshkovladyslav.transportexchangebackend.repo.LegalUserRepo;
import com.steshkovladyslav.transportexchangebackend.repo.UserRepo;
import com.steshkovladyslav.transportexchangebackend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private JwtUtils jwtUtils;

    private final UserRepo userRepo;
    private final LegalUserRepo legalUserRepo;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepo userRepo, LegalUserRepo legalUserRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.legalUserRepo = legalUserRepo;
        this.encoder = encoder;
    }

    public <T> T getUser(String jwtToken) {
        String email = jwtUtils.getUserNameFromJwtToken(jwtToken);

        if (jwtUtils.validateJwtToken(jwtToken)) {
            User user = userRepo.findByEmail(email);

            if (user != null) {
                return (T) user;
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

            if (personalData.getPhone() != null && !personalData.getPhone().equals(user.getPhone())) {
                user.setPhone(personalData.getPhone());
            }

            if (personalData.getEmail() != null && !personalData.getEmail().equals(user.getEmail())) {
                user.setEmail(personalData.getEmail());
            }

            if (photo != null && !photo.getOriginalFilename().isEmpty()) {
                setPhotoUser(photo, uuidFile, user);
            }

            userRepo.save(user);
            return ResponseEntity.ok("OK");
        }
        return null;
    }

    private void setPhotoUser(MultipartFile photo, String uuidFile, User user) throws IOException {
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

        String resultFileName = uuidFile + "." + photo.getOriginalFilename();
        photo.transferTo(new File(uploadPath + "/" + resultFileName));

        user.setProfilePicture(picturePath + resultFileName);
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
}
