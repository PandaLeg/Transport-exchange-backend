package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.*;
import com.steshkovladyslav.transportexchangebackend.payload.request.PropertiesRequest;
import com.steshkovladyslav.transportexchangebackend.payload.request.users.PersonalData;
import com.steshkovladyslav.transportexchangebackend.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${url.picture.path}")
    private String picturePath;

    @Autowired
    GeneralComponent generalComponent;

    private final CargoRepo cargoRepo;
    private final TransportRepo transportRepo;
    private final UserRepo userRepo;
    private final PointLUCargoRepo pointLUCargoRepo;
    private final PointLUTransRepo pointLUTransRepo;
    private final PhotoCargoRepo photoCargoRepo;
    private final PhotoTransportRepo photoTransportRepo;
    private final ConfirmationRepo confirmationRepo;

    @Autowired
    public AdminService(CargoRepo cargoRepo, TransportRepo transportRepo, UserRepo userRepo,
                        PointLUCargoRepo pointLUCargoRepo, PointLUTransRepo pointLUTransRepo,
                        PhotoCargoRepo photoCargoRepo, PhotoTransportRepo photoTransportRepo, ConfirmationRepo confirmationRepo) {
        this.cargoRepo = cargoRepo;
        this.transportRepo = transportRepo;
        this.userRepo = userRepo;
        this.pointLUCargoRepo = pointLUCargoRepo;
        this.pointLUTransRepo = pointLUTransRepo;
        this.photoCargoRepo = photoCargoRepo;
        this.photoTransportRepo = photoTransportRepo;
        this.confirmationRepo = confirmationRepo;
    }


    public Map<String, Integer> calculateStatisticsOfCargo() {
        List<Cargo> allCargo = cargoRepo.findAll();
        Map<Integer, String> dates = new LinkedHashMap<>();
        Map<String, Integer> monthsOfCargo = new LinkedHashMap<>();

        String month;
        String newMonth;

        int iteratorMonths = 0;

        String[] months = {"Ja", "Fe", "Ma", "Ap", "May", "Ju", "Jul", "Au", "Se", "Oc", "No", "De"};

        for (int i = 0; i < 12; i++) {
            dates.put(i + 1, months[i]);
            monthsOfCargo.put(months[i], 0);
        }

        for (int i = 0; i < allCargo.size(); i++) {
            month = dates.get(allCargo.get(i).getDateAdded().getMonth().getValue());

            if (i != 0) {
                newMonth = dates.get(allCargo.get(i - 1).getDateAdded().getMonth().getValue());
                // Если предыдущий груз отличается по месяцу от текущего, обнуляем
                if (!month.equals(newMonth)) {
                    iteratorMonths = 0;
                }
            }

            monthsOfCargo.replace(month, ++iteratorMonths);
        }

        return monthsOfCargo;
    }

    public Map<String, Integer> calculateStatisticsOfTransport() {
        List<Transport> allTransport = transportRepo.findAll();
        Map<Integer, String> dates = new LinkedHashMap<>();
        Map<String, Integer> monthsOfTransports = new LinkedHashMap<>();

        String month;
        String newMonth;

        int iteratorMonths = 0;

        String[] months = {"Ja", "Fe", "Ma", "Ap", "May", "Ju", "Jul", "Au", "Se", "Oc", "No", "De"};

        for (int i = 0; i < 12; i++) {
            dates.put(i + 1, months[i]);
            monthsOfTransports.put(months[i], 0);
        }


        for (int i = 0; i < allTransport.size(); i++) {
            month = dates.get(allTransport.get(i).getDateAdded().getMonth().getValue());

            if (i != 0) {
                newMonth = dates.get(allTransport.get(i - 1).getDateAdded().getMonth().getValue());
                // Если предыдущий груз отличается по месяцу от текущего, обнуляем
                if (!month.equals(newMonth)) {
                    iteratorMonths = 0;
                }
            }

            monthsOfTransports.replace(month, ++iteratorMonths);
        }

        return monthsOfTransports;
    }

    public Map<String, Integer> calculateStatisticsOfUsers() {
        List<User> users = userRepo.findAll();
        Map<Integer, String> dates = new LinkedHashMap<>();
        Map<String, Integer> monthsOfUsers = new LinkedHashMap<>();

        String month;
        String newMonth;

        int iteratorMonths = 0;

        String[] months = {"Ja", "Fe", "Ma", "Ap", "May", "Ju", "Jul", "Au", "Se", "Oc", "No", "De"};

        for (int i = 0; i < 12; i++) {
            dates.put(i + 1, months[i]);
            monthsOfUsers.put(months[i], 0);
        }

        for (int i = 0; i < users.size(); i++) {
            month = dates.get(users.get(i).getLastVisit().getMonth().getValue());

            if (i != 0) {
                newMonth = dates.get(users.get(i - 1).getLastVisit().getMonth().getValue());
                // Если предыдущий пользователь зарегистрировался в начале нового месяца, обнуляем
                if (!month.equals(newMonth)) {
                    iteratorMonths = 0;
                }
            }

            monthsOfUsers.replace(month, ++iteratorMonths);
        }

        return monthsOfUsers;
    }

    public Map<String, Object> calculateStatisticsCountOfCargoAndTransports() {
        Map<String, Object> mapCount = new HashMap<>();

        List<Cargo> cargo = cargoRepo.findAll();
        List<Transport> transports = transportRepo.findAll();

        List<Cargo> filteredCargo = new ArrayList<>();
        List<Transport> filteredTransport = new ArrayList<>();

        for (Cargo c : cargo) {
            if (c.getDateAdded().getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                filteredCargo.add(c);
            }
        }

        for (Transport t : transports) {
            if (t.getDateAdded().getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                filteredTransport.add(t);
            }
        }

        mapCount.put("countCargo", cargo.size());
        mapCount.put("countTransports", transports.size());
        mapCount.put("countCargoToday", filteredCargo.size());
        mapCount.put("countTransportsToday", filteredTransport.size());

        return mapCount;
    }

    public Map<String, Object> getUsers() {
        List<User> usersFromDb = userRepo.findAll();

        List<User> individualUsers = new ArrayList<>();
        List<User> legalUsers = new ArrayList<>();

        Map<String, Object> users = new HashMap<>();

        boolean checkAdmin = false;
        boolean checkUser = false;

        for (User user : usersFromDb) {
            List<String> roles = user.getRoles().stream().map(item -> item.getName().toString())
                    .collect(Collectors.toList());
            for (String role : roles) {
                if (role.equals("ROLE_ADMIN")) {
                    checkAdmin = true;
                }

                if (role.equals("ROLE_USER")) {
                    checkUser = true;
                }
            }

            if (!checkAdmin && checkUser) {
                individualUsers.add(user);
            }

            if (!checkAdmin && !checkUser) {
                legalUsers.add(user);
            }

            checkAdmin = false;
            checkUser = false;
        }

        users.put("individualUsers", individualUsers);
        users.put("legalUsers", legalUsers);

        return users;
    }

    public Map<String, Object> getCargoAndTransports() {
        Map<String, Object> resultMap = new HashMap<>();

        List<PointLUCargo> pointsCargoByIds;
        List<PointLUTransport> pointsTransportsByIds;
        List<PointLUCargo> filteredArrayCargo = new ArrayList<>();
        List<PointLUTransport> filteredArrayTransports = new ArrayList<>();

        Long id = 0L;

        List<Cargo> cargo = cargoRepo.findAll();
        List<Transport> transports = transportRepo.findAll();

        pointsCargoByIds = pointLUCargoRepo.findAllByIds(cargo.stream().map(Cargo::getId).collect(Collectors.toList()));
        pointsTransportsByIds = pointLUTransRepo.findAllByIds(transports.stream().map(Transport::getId)
                .collect(Collectors.toList()));

        // Убираем дубликаты
        for (PointLUCargo point : pointsCargoByIds) {
            if (!id.equals(point.getCargo().getId())) {
                filteredArrayCargo.add(point);
                id = point.getCargo().getId();
            }
        }

        id = 0L;

        for (PointLUTransport point : pointsTransportsByIds) {
            if (!id.equals(point.getTransport().getId())) {
                filteredArrayTransports.add(point);
                id = point.getTransport().getId();
            }
        }

        resultMap.put("cargo", cargo);
        resultMap.put("listPointsCargo", filteredArrayCargo);
        resultMap.put("transports", transports);
        resultMap.put("listPointsTransports", filteredArrayTransports);

        return resultMap;
    }

    public User getUserFromAdminPanel(long id) {
        return userRepo.findById(id);
    }

    public ResponseEntity<?> updatePersonalData(PersonalData personalData, MultipartFile photo) throws IOException {
        String uuidFile = UUID.randomUUID().toString();

        User user = userRepo.findById((long) personalData.getId());

        if (user != null) {
            List<String> collect = user.getRoles().stream().map(i -> i.getName().name()).collect(Collectors.toList());
            boolean role_user = collect.contains("ROLE_LEGAL_USER");

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

            if (role_user) {
                if (personalData.getCompanyName() != null && !personalData.getCompanyName().equals(user.getCompanyName())) {
                    user.setCompanyName(personalData.getCompanyName());
                }

                if (personalData.getCompanyCode() != null && !personalData.getCompanyCode().equals(user.getCompanyCode())) {
                    user.setCompanyCode(personalData.getCompanyCode());
                }
            }

            if (photo != null && !photo.getOriginalFilename().isEmpty()) {
                generalComponent.setPhotoUser(photo, uuidFile, user);
            }

            userRepo.save(user);
            return ResponseEntity.ok("OK");
        }
        return null;
    }

    public ResponseEntity<?> updateCargo(String typeTransportation, Cargo cargo, List<PointLUCargo> placesCargo,
                                         PropertiesRequest propertiesCargo, List<String> imagesUrl,
                                         MultipartFile firstFile, MultipartFile secondFile, MultipartFile thirdFile) throws IOException {
        Cargo cargoFromDb = cargoRepo.findById((long) cargo.getId());
        List<PhotoCargo> photosCargo = photoCargoRepo.findByCargo_Id(cargoFromDb.getId());

        boolean checkPhoto = false;
        String uuidFile = UUID.randomUUID().toString();
        String resultFileName = "";

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(firstFile);
        multipartFiles.add(secondFile);
        multipartFiles.add(thirdFile);

        if (cargo.getName() != null && !cargo.getName().equals(cargoFromDb.getName())) {
            cargoFromDb.setName(cargo.getName());
        }

        if (cargo.getWeightFrom() != null && !cargo.getWeightFrom().equals(cargoFromDb.getWeightFrom())) {
            cargoFromDb.setWeightFrom(cargo.getWeightFrom());
        }

        if (cargo.getWeightUpTo() != null && !cargo.getWeightUpTo().equals(cargoFromDb.getWeightUpTo())) {
            cargoFromDb.setWeightUpTo(cargo.getWeightUpTo());
        }

        if (cargo.getVolumeFrom() != null && !cargo.getVolumeFrom().equals(cargoFromDb.getVolumeFrom())) {
            cargoFromDb.setVolumeFrom(cargo.getVolumeFrom());
        }

        if (cargo.getVolumeUpTo() != null && !cargo.getVolumeUpTo().equals(cargoFromDb.getVolumeUpTo())) {
            cargoFromDb.setVolumeUpTo(cargo.getVolumeUpTo());
        }

        if (cargo.getLengthCargo() != null && !cargo.getLengthCargo().equals(cargoFromDb.getLengthCargo())) {
            cargoFromDb.setLengthCargo(cargo.getLengthCargo());
        }

        if (cargo.getWidthCargo() != null && !cargo.getWidthCargo().equals(cargoFromDb.getWidthCargo())) {
            cargoFromDb.setWidthCargo(cargo.getWidthCargo());
        }

        if (cargo.getHeightCargo() != null && !cargo.getHeightCargo().equals(cargoFromDb.getHeightCargo())) {
            cargoFromDb.setHeightCargo(cargo.getHeightCargo());
        }

        if (cargo.getAdr() != null && !cargo.getAdr().equals(cargoFromDb.getAdr())) {
            cargoFromDb.setAdr(cargo.getAdr());
        }

        if (cargo.getLoadingDateFrom() != null && !cargo.getLoadingDateFrom().equals(cargoFromDb.getLoadingDateFrom())) {
            cargoFromDb.setLoadingDateFrom(cargo.getLoadingDateFrom());
        }

        if (cargo.getLoadingDateBy() != null && !cargo.getLoadingDateBy().equals(cargoFromDb.getLoadingDateBy())) {
            cargoFromDb.setLoadingDateBy(cargo.getLoadingDateBy());
        }

        if (cargo.getBodyType() != null && !cargo.getBodyType().equals(cargoFromDb.getBodyType())) {
            cargoFromDb.setBodyType(cargo.getBodyType());
        }

        if (cargo.getIncoterms() != null && !cargo.getIncoterms().equals(cargoFromDb.getIncoterms())) {
            cargoFromDb.setIncoterms(cargo.getIncoterms());
        }

        if (cargo.getCost() != null && !cargo.getCost().equals(cargoFromDb.getCost())) {
            cargoFromDb.setCost(cargo.getCost());
        }

        if (cargo.getCurrency() != null && !cargo.getCurrency().equals(cargoFromDb.getCurrency())) {
            cargoFromDb.setCurrency(cargo.getCurrency());
        }

        if (cargo.getPrepayment() != null && !cargo.getPrepayment().equals(cargoFromDb.getPrepayment())) {
            cargoFromDb.setPrepayment(cargo.getPrepayment());
        }

        if (cargo.getAdditional() != null && !cargo.getAdditional().equals(cargoFromDb.getAdditional())) {
            cargoFromDb.setAdditional(cargo.getAdditional());
        }

        cargoFromDb.getPropertiesCargo().clear();
        generalComponent.updatePropertiesCargo(cargoFromDb, propertiesCargo);

        cargoFromDb.getPointsCargo().clear();
        generalComponent.updatePlacesCargo(cargoFromDb, placesCargo);

        if (firstFile != null || secondFile != null || thirdFile != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            if (photosCargo.size() != 0 && imagesUrl.size() != 0) {
                // Удаляем те картинки, которые были отредактированы
                for (PhotoCargo photoCargo : photosCargo) {
                    for (String url : imagesUrl) {
                        if (photoCargo.getPhotoUrl().equals(url)) {
                            checkPhoto = true;
                        }
                    }
                    if (!checkPhoto) {
                        int i = photoCargo.getPhotoUrl().lastIndexOf('/');
                        String tempUrlPicture = photoCargo.getPhotoUrl().substring(i);

                        File file = new File(uploadPath + "/" + tempUrlPicture);

                        if (file.delete()) {
                            System.out.println("Успешно удален");
                        } else {
                            System.out.println("Ошибка, файл не был удалён");
                        }

                        cargoFromDb.getPhotoCargo().remove(photoCargo);
                    }
                    checkPhoto = false;
                }
            }

            for (MultipartFile file : multipartFiles) {
                if (file != null && !file.getOriginalFilename().isEmpty()) {
                    resultFileName = uuidFile + "." + file.getOriginalFilename();
                    file.transferTo(new File(uploadPath + "/" + resultFileName));

                    PhotoCargo photoCargo = new PhotoCargo(picturePath + resultFileName);
                    photoCargo.setCargo(cargo);
                    photoCargoRepo.save(photoCargo);
                }
            }
        }

        cargoRepo.save(cargoFromDb);

        return ResponseEntity.ok("OK");
    }

    public ResponseEntity<?> updateTransport(Transport transport, List<PointLUTransport> placesTransport,
                                             PropertiesRequest propertiesTransport, List<String> imagesUrl,
                                             MultipartFile firstFile, MultipartFile secondFile, MultipartFile thirdFile)
            throws IOException {
        Transport transportFromDb = transportRepo.findById((long) transport.getId());
        List<PhotoTransport> photoTransports = photoTransportRepo.findByTransport_Id(transportFromDb.getId());

        boolean checkPhoto = false;
        String uuidFile = UUID.randomUUID().toString();
        String resultFileName = "";

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(firstFile);
        multipartFiles.add(secondFile);
        multipartFiles.add(thirdFile);

        if (transport.getBodyType() != null && !transport.getBodyType().equals(transportFromDb.getBodyType())) {
            transportFromDb.setBodyType(transport.getBodyType());
        }

        if (transport.getCarryingCapacityFrom() != null && !transport.getCarryingCapacityFrom()
                .equals(transportFromDb.getCarryingCapacityFrom())) {
            transportFromDb.setCarryingCapacityFrom(transport.getCarryingCapacityFrom());
        }

        if (transport.getCarryingCapacityUpTo() != null && !transport.getCarryingCapacityUpTo()
                .equals(transportFromDb.getCarryingCapacityUpTo())) {
            transportFromDb.setCarryingCapacityUpTo(transport.getCarryingCapacityUpTo());
        }

        if (transport.getVolumeFrom() != null && !transport.getVolumeFrom().equals(transportFromDb.getVolumeFrom())) {
            transportFromDb.setVolumeFrom(transport.getVolumeFrom());
        }

        if (transport.getVolumeUpTo() != null && !transport.getVolumeUpTo().equals(transportFromDb.getVolumeUpTo())) {
            transportFromDb.setVolumeUpTo(transport.getVolumeUpTo());
        }

        if (transport.getLengthTransport() != null && !transport.getLengthTransport()
                .equals(transportFromDb.getLengthTransport())) {
            transportFromDb.setLengthTransport(transport.getLengthTransport());
        }

        if (transport.getWidthTransport() != null && !transport.getWidthTransport()
                .equals(transportFromDb.getWidthTransport())) {
            transportFromDb.setWidthTransport(transport.getWidthTransport());
        }

        if (transport.getHeightTransport() != null && !transport.getHeightTransport()
                .equals(transportFromDb.getHeightTransport())) {
            transportFromDb.setHeightTransport(transport.getHeightTransport());
        }

        if (transport.getLoadingDateFrom() != null && !transport.getLoadingDateFrom()
                .equals(transportFromDb.getLoadingDateFrom())) {
            transportFromDb.setLoadingDateFrom(transport.getLoadingDateFrom());
        }

        if (transport.getLoadingDateBy() != null && !transport.getLoadingDateBy()
                .equals(transportFromDb.getLoadingDateBy())) {
            transportFromDb.setLoadingDateBy(transport.getLoadingDateBy());
        }

        if (transport.getCost() != null && !transport.getCost().equals(transportFromDb.getCost())) {
            transportFromDb.setCost(transport.getCost());
        }

        if (transport.getCurrency() != null && !transport.getCurrency().equals(transportFromDb.getCurrency())) {
            transportFromDb.setCurrency(transport.getCurrency());
        }

        if (transport.getPrepayment() != null && !transport.getPrepayment().equals(transportFromDb.getPrepayment())) {
            transportFromDb.setPrepayment(transport.getPrepayment());
        }

        if (transport.getAdditional() != null && !transport.getAdditional().equals(transportFromDb.getAdditional())) {
            transportFromDb.setAdditional(transport.getAdditional());
        }

        transportFromDb.getPropertiesTransport().clear();
        generalComponent.updatePropertiesTransport(transportFromDb, propertiesTransport);

        transportFromDb.getPointsTransports().clear();
        generalComponent.updatePlacesTransport(transportFromDb, placesTransport);

        if (firstFile != null || secondFile != null || thirdFile != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            if (photoTransports.size() != 0 && imagesUrl.size() != 0) {
                // Удаляем те картинки, которые были отредактированы
                for (PhotoTransport photoTransport : photoTransports) {
                    for (String url : imagesUrl) {
                        if (photoTransport.getPhotoUrl().equals(url)) {
                            checkPhoto = true;
                        }
                    }
                    if (!checkPhoto) {
                        int i = photoTransport.getPhotoUrl().lastIndexOf('/');
                        String tempUrlPicture = photoTransport.getPhotoUrl().substring(i);

                        File file = new File(uploadPath + "/" + tempUrlPicture);

                        if (file.delete()) {
                            System.out.println("Успешно удален");
                        } else {
                            System.out.println("Ошибка, файл не был удалён");
                        }

                        transportFromDb.getPhotoTransport().remove(photoTransport);
                    }
                    checkPhoto = false;
                }
            }

            for (MultipartFile file : multipartFiles) {
                if (file != null && !file.getOriginalFilename().isEmpty()) {
                    resultFileName = uuidFile + "." + file.getOriginalFilename();
                    file.transferTo(new File(uploadPath + "/" + resultFileName));

                    PhotoTransport photoTransport = new PhotoTransport(picturePath + resultFileName);
                    photoTransport.setTransport(transport);
                    photoTransportRepo.save(photoTransport);
                }
            }
        }

        transportRepo.save(transportFromDb);

        return ResponseEntity.ok("OK");
    }


    public Map<String, Object> getConfirmations(int page, int pageSize) {
        Map<String, Object> resultMap = new HashMap<>();
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Confirmation> confirmations = confirmationRepo.findAll(pageable);

        resultMap.put("confirmations", confirmations.getContent());
        resultMap.put("currentPage", confirmations.getNumber());
        resultMap.put("totalPages", confirmations.getTotalPages());

        return resultMap;
    }


    public Confirmation getConfirmation(long id) {
        if (id != 0) {
            return confirmationRepo.findById(id);
        }
        return null;
    }

    public ResponseEntity<?> confirmationCompany(long id) {
        Confirmation confirmation = confirmationRepo.findById(id);
        User user = userRepo.findById((long) confirmation.getUser().getId());

        user.setStatus("Confirmed");

        user.setConfirmation(null);

        userRepo.save(user);

        return ResponseEntity.ok("OK");
    }
}
