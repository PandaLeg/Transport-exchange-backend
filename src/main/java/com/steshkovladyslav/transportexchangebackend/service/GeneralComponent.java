package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.*;
import com.steshkovladyslav.transportexchangebackend.payload.request.PropertiesRequest;
import com.steshkovladyslav.transportexchangebackend.repo.*;
import com.steshkovladyslav.transportexchangebackend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class GeneralComponent {
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${url.picture.path}")
    private String picturePath;

    @Autowired
    private JwtUtils jwtUtils;

    private final UserRepo userRepo;
    private final LegalUserRepo legalUserRepo;
    private final PropertyRepo propertyRepo;
    private final PointLUCargoRepo pointLURepo;
    private final PointLUTransRepo pointLUTransRepo;

    @Autowired
    public GeneralComponent(UserRepo userRepo, LegalUserRepo legalUserRepo, PropertyRepo propertyRepo,
                            PointLUCargoRepo pointLURepo, PointLUTransRepo pointLUTransRepo) {
        this.userRepo = userRepo;
        this.legalUserRepo = legalUserRepo;
        this.propertyRepo = propertyRepo;
        this.pointLURepo = pointLURepo;
        this.pointLUTransRepo = pointLUTransRepo;
    }

    boolean setUser(String token, Cargo cargo, Transport transport) {
        User user = null;

        if (jwtUtils.validateJwtToken(token)) {
            String email = jwtUtils.getUserNameFromJwtToken(token);

            if (email != null) {
                user = userRepo.findByEmail(email);
            }

            if (user != null) {
                if (cargo != null) {
                    cargo.setUser(user);
                } else {
                    transport.setUser(user);
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    void setPhotoUser(MultipartFile photo, String uuidFile, User user) throws IOException {
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


    void updatePropertiesCargo(Cargo cargo, PropertiesRequest propertiesCargo) {
        if (propertiesCargo.getTypesLoadingTruck() != null) {
            for (String loading : propertiesCargo.getTypesLoadingTruck()) {
                Property property = propertyRepo.findByNameAndProperty(loading, "loading");
                cargo.getPropertiesCargo().add(property);
            }
        }

        if (propertiesCargo.getTypesUnloadingTruck() != null) {
            for (String unloading : propertiesCargo.getTypesUnloadingTruck()) {
                Property property = propertyRepo.findByNameAndProperty(unloading, "unloading");
                cargo.getPropertiesCargo().add(property);
            }
        }

        if (propertiesCargo.getContainerLoading() != null) {
            for (String containerLoading : propertiesCargo.getContainerLoading()) {
                Property property = propertyRepo.findByNameAndProperty(containerLoading, "containerLoading");
                cargo.getPropertiesCargo().add(property);
            }
        }

        if (propertiesCargo.getPermissions() != null) {
            System.out.println(propertiesCargo.getPermissions());
            for (String permission : propertiesCargo.getPermissions()) {
                Property property = propertyRepo.findByName(permission);
                cargo.getPropertiesCargo().add(property);
            }
        }

        if (propertiesCargo.getTypePayment() != null && !propertiesCargo.getTypePayment().equals("")) {
            Property property = propertyRepo.findByName(propertiesCargo.getTypePayment());
            cargo.getPropertiesCargo().add(property);
        }

        if (propertiesCargo.getCostPer() != null && !propertiesCargo.getCostPer().equals("")) {
            Property property = propertyRepo.findByName(propertiesCargo.getCostPer());
            cargo.getPropertiesCargo().add(property);
        }

        if (propertiesCargo.getPaymentForm() != null && !propertiesCargo.getPaymentForm().equals("")) {
            Property property = propertyRepo.findByName(propertiesCargo.getPaymentForm());
            cargo.getPropertiesCargo().add(property);
        }

        if (propertiesCargo.getPaymentTime() != null && !propertiesCargo.getPaymentTime().equals("")) {
            Property property = propertyRepo.findByName(propertiesCargo.getPaymentTime());
            cargo.getPropertiesCargo().add(property);
        }
    }

    void updatePlacesCargo(Cargo cargo, List<PointLUCargo> placesCargo) {
        for (PointLUCargo pointLUCargo : placesCargo) {
            if (pointLUCargo.getCityFrom() != null && pointLUCargo.getCountryFrom() != null ||
                    pointLUCargo.getCityTo() != null && pointLUCargo.getCountryTo() != null) {
                pointLUCargo.setCargo(cargo);
                pointLURepo.save(pointLUCargo);
            }
        }
    }

    void updatePropertiesTransport(Transport transport, PropertiesRequest propertiesTransport) {
        if (propertiesTransport.getTypesLoadingTruck() != null) {
            for (String loading : propertiesTransport.getTypesLoadingTruck()) {
                System.out.println(loading);
                Property byRuName = propertyRepo.findByNameAndProperty(loading, "loading");
                transport.getPropertiesTransport().add(byRuName);
            }
        }

        System.out.println(propertiesTransport.getTypesUnloadingTruck());
        if (propertiesTransport.getTypesUnloadingTruck() != null) {
            for (String unloading : propertiesTransport.getTypesUnloadingTruck()) {
                System.out.println(unloading);
                Property byRuName = propertyRepo.findByNameAndProperty(unloading, "unloading");
                transport.getPropertiesTransport().add(byRuName);
            }
        }

        if (propertiesTransport.getPermissions() != null) {
            for (String permission : propertiesTransport.getPermissions()) {
                Property byRuName = propertyRepo.findByName(permission);
                transport.getPropertiesTransport().add(byRuName);
            }
        }

        if (propertiesTransport.getTypePayment() != null && !propertiesTransport.getTypePayment().equals("")) {
            Property byRuName = propertyRepo.findByName(propertiesTransport.getTypePayment());
            transport.getPropertiesTransport().add(byRuName);
        }

        if (propertiesTransport.getCostPer() != null && !propertiesTransport.getCostPer().equals("")) {
            Property byRuName = propertyRepo.findByName(propertiesTransport.getCostPer());
            transport.getPropertiesTransport().add(byRuName);
        }

        if (propertiesTransport.getPaymentForm() != null && !propertiesTransport.getPaymentForm().equals("")) {
            Property byRuName = propertyRepo.findByName(propertiesTransport.getPaymentForm());
            transport.getPropertiesTransport().add(byRuName);
        }

        if (propertiesTransport.getPaymentTime() != null && !propertiesTransport.getPaymentTime().equals("")) {
            Property byRuName = propertyRepo.findByName(propertiesTransport.getPaymentTime());
            transport.getPropertiesTransport().add(byRuName);
        }
    }

    void updatePlacesTransport(Transport transport, List<PointLUTransport> placesTransport) {
        for (PointLUTransport pointLUTransport : placesTransport) {
            if (pointLUTransport.getCityFrom() != null && pointLUTransport.getCountryFrom() != null ||
                    pointLUTransport.getCityTo() != null && pointLUTransport.getCountryTo() != null) {
                pointLUTransport.setTransport(transport);
                pointLUTransRepo.save(pointLUTransport);
            }
        }
    }
}

