package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.*;
import com.steshkovladyslav.transportexchangebackend.payload.request.PropertiesRequest;
import com.steshkovladyslav.transportexchangebackend.payload.request.cargo.CargoRequest;
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
public class CargoService {
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${url.picture.path}")
    private String picturePath;

    @Autowired
    GeneralComponent generalComponent;

    private final UserRepo userRepo;

    private final CargoRepo cargoRepo;
    private final PhotoCargoRepo photoCargoRepo;
    private final PropertyRepo propertyRepo;
    private final PointLUCargoRepo pointLURepo;
    private final CargoOfferRepo cargoOfferRepo;

    @Autowired
    public CargoService(UserRepo userRepo, CargoRepo cargoRepo, PhotoCargoRepo photoCargoRepo,
                        PropertyRepo propertyRepo, PointLUCargoRepo pointLURepo,
                        CargoOfferRepo cargoOfferRepo) {
        this.userRepo = userRepo;
        this.cargoRepo = cargoRepo;
        this.photoCargoRepo = photoCargoRepo;
        this.propertyRepo = propertyRepo;
        this.pointLURepo = pointLURepo;
        this.cargoOfferRepo = cargoOfferRepo;
    }

    public Cargo addCargo(
            String token,
            String typeTransportation,
            Cargo cargo,
            PropertiesRequest propertiesCargo,
            List<PointLUCargo> placesCargo,
            MultipartFile firstFile,
            MultipartFile secondFile,
            MultipartFile thirdFile
    ) {
        try {
            return saveCargo(token, typeTransportation, cargo, propertiesCargo, placesCargo, firstFile, secondFile,
                    thirdFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Cargo> getAllCargo() {
        return cargoRepo.findAll();
    }


    private Cargo saveCargo(
            String token,
            String typeTransportation,
            Cargo cargo,
            PropertiesRequest propertiesCargo,
            List<PointLUCargo> placesCargo,
            MultipartFile firstFile,
            MultipartFile secondFile,
            MultipartFile thirdFile
    ) throws IOException {
        String uuidFile = UUID.randomUUID().toString();
        String resultFileName = "";
        PointLUCargo pointLUCargo1 = placesCargo.get(0);

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(firstFile);
        multipartFiles.add(secondFile);
        multipartFiles.add(thirdFile);

        if (cargo != null) {
            if (!generalComponent.setUser(token, cargo, null))
                return null;

            if (pointLUCargo1 == null) {
                return null;
            }
            cargo.setTypeTransportation(typeTransportation);
            cargo.setDateAdded(LocalDateTime.now());

            generalComponent.updatePropertiesCargo(cargo, propertiesCargo);
            cargoRepo.save(cargo);
            generalComponent.updatePlacesCargo(cargo, placesCargo);
            cargoRepo.save(cargo);

            if (firstFile != null || secondFile != null || thirdFile != null) {
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
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
            return cargo;
        }
        return null;
    }

    public Map<String, Object> searchCargo(CargoRequest cargoRequest, int page, int pageSize) {
        List<Cargo> cargo;
        Set<Long> tempCargoId;
        List<Long> resultId;
        List<String> typesTransportation = new ArrayList<>();

        Page<Cargo> pageCargo;
        Map<String, Object> cargoMap = new HashMap<>();

        Pageable pageable = PageRequest.of(page, pageSize);

        replacePlusOnSpace(cargoRequest);

        // Формируем список типов перевозки
        for (String transportation : cargoRequest.getTypesTransportation()) {
            if (transportation.equals("Автоперевозка") || transportation.equals("Road transportation") ||
                    transportation.equals("Автоперевезення")) {
                typesTransportation.add("roadTransportation");
            }
            if (transportation.equals("Морская перевозка") || transportation.equals("Sea transportation") ||
                    transportation.equals("Морське перевезення")) {
                typesTransportation.add("seaTransportation");
            }

            if (transportation.equals("Ж/Д перевозка") || transportation.equals("Railway transportation") ||
                    transportation.equals("Залізничне перевезення")) {
                typesTransportation.add("railwayTransportation");
            }
        }

        // Находим совпадение по странам и городам
        tempCargoId = cargoRepo.getCargoIds(cargoRequest.getCountryFrom(), cargoRequest.getCityFrom(),
                cargoRequest.getCountryTo(), cargoRequest.getCityTo());

        if (cargoRequest.getLoadingDateFrom() == null && cargoRequest.getLoadingDateBy() == null) {
            pageCargo = cargoRepo.searchCargoWithParams(typesTransportation, tempCargoId,
                    cargoRequest.getWeightFrom(), cargoRequest.getWeightUpTo(), cargoRequest.getVolumeFrom(),
                    cargoRequest.getVolumeUpTo(), cargoRequest.getNameCargo(), cargoRequest.getBodyType(), pageable);

            cargo = pageCargo.getContent();

            resultId = getIdCargoPlaces(cargo);
        } else {
            pageCargo = getCargoByDate(cargoRequest, tempCargoId, typesTransportation, pageable);

            cargo = pageCargo.getContent();

            resultId = getIdCargoPlaces(cargo);

            if (cargo.isEmpty()) {
                cargo = new ArrayList<>();
                cargoMap.put("cargo", cargo);
                return cargoMap;
            }

            if (cargoRequest.getPaymentForm() == null && cargoRequest.getPaymentTime() == null) {
                filledCargoMap(cargoMap, cargo, pointLURepo.findAllByIds(resultId), pageCargo);
                return cargoMap;
            }
        }

        if (cargoRequest.getPaymentForm() != null || cargoRequest.getPaymentTime() != null) {
            cargo = checkPaymentForm(cargoRequest, cargo);

            if (cargo != null) {
                // Возврат всех мест загрузки и разгрузки груза
                resultId = getIdCargoPlaces(cargo);

                filledCargoMap(cargoMap, cargo, pointLURepo.findAllByIds(resultId), pageCargo);
                return cargoMap;
            } else {
                cargo = new ArrayList<>();
                cargoMap.put("cargo", cargo);
                return cargoMap;
            }
        }

        filledCargoMap(cargoMap, cargo, pointLURepo.findAllByIds(resultId), pageCargo);
        return cargoMap;
    }

    private List<Long> getIdCargoPlaces(List<Cargo> cargo) {
        return cargo.stream().map(Cargo::getId).collect(Collectors.toList());
    }

    private Page<Cargo> getCargoByDate(CargoRequest cargoRequest, Set<Long> tempCargoId, List<String> typesTransportation,
                                       Pageable pageable) {
        if (cargoRequest.getLoadingDateFrom() != null && cargoRequest.getLoadingDateBy() == null) {
            return cargoRepo.findByLoadingDateFrom(typesTransportation, tempCargoId, cargoRequest.getLoadingDateFrom(),
                    cargoRequest.getWeightFrom(), cargoRequest.getWeightUpTo(), cargoRequest.getVolumeFrom(),
                    cargoRequest.getVolumeUpTo(), cargoRequest.getNameCargo(), cargoRequest.getBodyType(),
                    pageable);
        } else if (cargoRequest.getLoadingDateBy() != null && cargoRequest.getLoadingDateFrom() == null) {
            return cargoRepo.findByLoadingDateBy(typesTransportation, tempCargoId, cargoRequest.getLoadingDateBy(),
                    cargoRequest.getWeightFrom(), cargoRequest.getWeightUpTo(), cargoRequest.getVolumeFrom(),
                    cargoRequest.getVolumeUpTo(), cargoRequest.getNameCargo(), cargoRequest.getBodyType(),
                    pageable);
        } else {
            return cargoRepo.getAllBetweenTwoDate(typesTransportation, tempCargoId, cargoRequest.getLoadingDateFrom(),
                    cargoRequest.getLoadingDateBy(), cargoRequest.getWeightFrom(), cargoRequest.getWeightUpTo(),
                    cargoRequest.getVolumeFrom(), cargoRequest.getVolumeUpTo(), cargoRequest.getNameCargo(),
                    cargoRequest.getBodyType(), pageable);
        }
    }

    private List<Cargo> checkPaymentForm(CargoRequest cargoRequest, List<Cargo> cargo) {
        List<Cargo> paymentFormList = new ArrayList<>();
        List<Cargo> paymentTimeList = new ArrayList<>();

        // Если заполнена только форма оплаты
        if (cargoRequest.getPaymentForm() != null && cargoRequest.getPaymentTime() == null) {
            for (Cargo item : cargo) {
                Property property = getProperty(item, "paymentForm");

                if (property != null) {
                    if (property.getRuName().equals(cargoRequest.getPaymentForm()) ||
                            property.getEnName().equals(cargoRequest.getPaymentForm()) ||
                            property.getUaName().equals(cargoRequest.getPaymentForm())) {
                        paymentFormList.add(item);
                    }
                }
            }

            if (paymentFormList.isEmpty()) {
                cargo = null;
            }
            // Если заполнено только поле момента оплаты
        } else if (cargoRequest.getPaymentTime() != null && cargoRequest.getPaymentForm() == null) {
            // Заполнение грузов при заполненном моменте оплаты
            for (Cargo item : cargo) {
                Property property = getProperty(item, "paymentTime");

                if (property != null) {
                    if (property.getRuName().equals(cargoRequest.getPaymentTime()) ||
                            property.getEnName().equals(cargoRequest.getPaymentTime()) ||
                            property.getUaName().equals(cargoRequest.getPaymentTime())) {
                        paymentTimeList.add(item);
                    }
                }
            }

            if (paymentTimeList.isEmpty()) {
                cargo = null;
            }
            // Если заполнены оба поля
        } else {
            System.out.println("ДАТА ЗАПОЛНЕНА");
            for (Cargo item : cargo) {
                Property property = getProperty(item, "paymentForm");

                if (property != null) {
                    if (property.getRuName().equals(cargoRequest.getPaymentForm()) ||
                            property.getEnName().equals(cargoRequest.getPaymentForm()) ||
                            property.getUaName().equals(cargoRequest.getPaymentForm())) {
                        paymentFormList.add(item);
                    }
                }
            }

            for (Cargo item : cargo) {
                Property property = getProperty(item, "paymentTime");

                if (property != null) {
                    if (property.getRuName().equals(cargoRequest.getPaymentTime()) ||
                            property.getEnName().equals(cargoRequest.getPaymentTime()) ||
                            property.getUaName().equals(cargoRequest.getPaymentTime())) {
                        paymentTimeList.add(item);
                    }
                }
            }

            if (paymentFormList.isEmpty() || paymentTimeList.isEmpty()) {
                cargo = null;
            }

            if (!paymentFormList.isEmpty() && !paymentTimeList.isEmpty()) {
                boolean checkFilledFormAndTimeList = true;
                for (Cargo item : paymentFormList) {
                    for (Cargo value : paymentTimeList) {
                        if (item.getId().equals(value.getId())) {
                            if (checkFilledFormAndTimeList) {
                                cargo = new ArrayList<>();
                                checkFilledFormAndTimeList = false;
                            }
                            cargo.add(item);
                        }
                    }
                }
            }

            paymentFormList.clear();
            paymentTimeList.clear();
        }

        if (!paymentFormList.isEmpty()) {
            cargo = new ArrayList<>(paymentFormList);
        }

        if (!paymentTimeList.isEmpty()) {
            cargo = new ArrayList<>(paymentTimeList);
        }

        return cargo;
    }

    private Property getProperty(Cargo item, String payment) {
        return item.getPropertiesCargo().stream()
                .filter(i -> payment.equals(i.getProperty()))
                .findFirst().orElse(null);
    }

    private void replacePlusOnSpace(CargoRequest cargoRequest) {
        if (cargoRequest.getCountryFrom() != null)
            cargoRequest.setCountryFrom(cargoRequest.getCountryFrom().replaceAll("\\+", " "));

        if (cargoRequest.getCountryTo() != null)
            cargoRequest.setCountryTo(cargoRequest.getCountryTo().replaceAll("\\+", " "));

        if (cargoRequest.getCityFrom() != null)
            cargoRequest.setCityFrom(cargoRequest.getCityFrom().replaceAll("\\+", " "));

        if (cargoRequest.getCityTo() != null)
            cargoRequest.setCityTo(cargoRequest.getCityTo().replaceAll("\\+", " "));

        if (cargoRequest.getNameCargo() != null)
            cargoRequest.setNameCargo(cargoRequest.getNameCargo().replaceAll("\\+", " "));

        if (cargoRequest.getBodyType() != null)
            cargoRequest.setBodyType(cargoRequest.getBodyType().replaceAll("\\+", " "));
    }

    private void filledCargoMap(Map<String, Object> cargoMap,
                                List<Cargo> cargo,
                                List<PointLUCargo> pointsLUCargo,
                                Page<Cargo> pageCargo) {
        cargoMap.put("cargo", cargo);
        cargoMap.put("pointsLUCargo", pointsLUCargo);
        cargoMap.put("currentPage", pageCargo.getNumber());
        cargoMap.put("totalPages", pageCargo.getTotalPages());
    }

    public Map<String, Object> getCargo(long id) {
        Map<String, Object> cargo = new HashMap<>();

        Cargo cargoFromDb = cargoRepo.findById(id);
        List<PointLUCargo> pointsLUCargoById = pointLURepo.getPointsLUCargoById(id);

        User user = userRepo.findById((long) cargoFromDb.getUser().getId());

        cargo.put("user", user);
        cargo.put("cargo", cargoFromDb);
        cargo.put("pointsLUCargo", pointsLUCargoById);

        return cargo;
    }

    public List<PointLUCargo> getPointsCargo(long id) {
        return pointLURepo.getPointsLUCargoById(id);
    }

    public List<PhotoCargo> getPhotoCargo(long id) {
        return photoCargoRepo.findByCargo_Id(id);
    }

    public Integer getCountCargo(long id, String role) {
        System.out.println(id);

        List<Cargo> cargo = cargoRepo.findAllByUser_Id(id);
        return cargo.size();
    }

    public ResponseEntity<?> addCargoOffer(long idCargo, CargoOffer cargoOffer, String role, long idUser) {
        Cargo cargo;

        if (idCargo != 0) {
            cargo = cargoRepo.findById(idCargo);
            cargoOffer.setCargo(cargo);

            User user = userRepo.findById(idUser);

            if (user != null) {
                cargoOffer.setUser(user);
                cargoOfferRepo.save(cargoOffer);
            } else {
                return null;
            }

            return ResponseEntity.ok("OK");
        }

        return (ResponseEntity<?>) ResponseEntity.noContent();
    }

    public Map<String, Object> getAllOfferCargo(long id, String role) {
        Map<String, Object> cargo = new HashMap<>();
        List<PointLUCargo> filteredArray = new ArrayList<>();
        List<PointLUCargo> allByIds;
        Long idCargo = 0L;

        if (id != 0L) {
            List<Cargo> allByUser_id = cargoRepo.findAllByUser_Id(id);
            allByIds = pointLURepo.findAllByIds(allByUser_id.stream().map(Cargo::getId)
                    .collect(Collectors.toList()));

            // Убираем дубликаты
            for (PointLUCargo point : allByIds) {
                if (!idCargo.equals(point.getCargo().getId())) {
                    filteredArray.add(point);
                    idCargo = point.getCargo().getId();
                }
            }

            cargo.put("cargo", allByUser_id);
            cargo.put("pointsLUCargo", filteredArray);
            return cargo;
        }

        return null;
    }

    public Map<String, Object> getActiveAndSentOffersCargo(long id, String role) {
        Map<String, Object> cargo = new HashMap<>();
        List<CargoOffer> cargoOffers;
        List<Cargo> allCargoFromOffers;

        List<Cargo> allCargoSend = new ArrayList<>();
        List<Cargo> allCargoActive = new ArrayList<>();
        List<Cargo> allCargoInProcessing = new ArrayList<>();
        List<Cargo> allCargoComplete = new ArrayList<>();

        List<PointLUCargo> filteredPointsDispatchedCargo = new ArrayList<>();
        List<PointLUCargo> filteredPointsActiveCargo = new ArrayList<>();
        List<PointLUCargo> filteredPointsInProcessingCargo = new ArrayList<>();
        List<PointLUCargo> filteredPointsCompleteCargo = new ArrayList<>();

        Long idCargo = 0L;

        if (id != 0L) {
            cargoOffers = cargoOfferRepo.findAll();
            allCargoFromOffers = cargoRepo.getByCargoId();

            for (CargoOffer cargoOffer : cargoOffers) {
                // Заявка, которая была отправлена юзером
                if (cargoOffer.getUser() != null) {
                    if (cargoOffer.getUser().getId() == id) {
                        if (cargoOffer.getCargo().getStatus() != null &&
                                !cargoOffer.getCargo().getStatus().equals("Complete")) {
                            allCargoInProcessing.add(cargoOffer.getCargo());
                        } else if (cargoOffer.getCargo().getStatus() != null &&
                                cargoOffer.getCargo().getStatus().equals("Complete")) {
                            allCargoComplete.add(cargoOffer.getCargo());
                        } else {
                            allCargoSend.add(cargoOffer.getCargo());
                        }
                    }
                }
            }

            for (Cargo c : allCargoFromOffers) {
                // Заявка которую отправили юзеру
                if (c.getUser() != null) {
                    if (c.getUser().getId() == id) {
                        if (c.getStatus() != null && !c.getStatus().equals("Complete")) {
                            allCargoInProcessing.add(c);
                        } else if (c.getStatus() != null && c.getStatus().equals("Complete")) {
                            allCargoComplete.add(c);
                        } else {
                            allCargoActive.add(c);
                        }
                    }
                }
            }

            setPointsCargoAndFilledCargoMap(cargo, allCargoSend, allCargoActive, allCargoInProcessing, allCargoComplete,
                    filteredPointsDispatchedCargo, filteredPointsActiveCargo, filteredPointsInProcessingCargo,
                    filteredPointsCompleteCargo, idCargo);

            return cargo;
        }

        return null;
    }

    private void setPointsCargoAndFilledCargoMap(Map<String, Object> cargo, List<Cargo> allCargoSend,
                                                 List<Cargo> allCargoActive, List<Cargo> allCargoInProcessing,
                                                 List<Cargo> allCargoComplete,
                                                 List<PointLUCargo> filteredPointsDispatchedCargo,
                                                 List<PointLUCargo> filteredPointsActiveCargo,
                                                 List<PointLUCargo> filteredPointsInProcessingCargo,
                                                 List<PointLUCargo> filteredPointsCompleteCargo, Long idCargo) {
        List<PointLUCargo> allPointsDispatchedCargo;
        List<PointLUCargo> allPointsActiveCargo;
        List<PointLUCargo> allPointsCargoInProcessing;
        List<PointLUCargo> allPointsCargoComplete;

        allPointsDispatchedCargo = pointLURepo.findAllByIds(allCargoSend.stream().map(Cargo::getId)
                .collect(Collectors.toList()));

        allPointsActiveCargo = pointLURepo.findAllByIds(allCargoActive.stream().map(Cargo::getId)
                .collect(Collectors.toList()));

        allPointsCargoInProcessing = pointLURepo.findAllByIds(allCargoInProcessing.stream().map(Cargo::getId)
                .collect(Collectors.toList()));

        allPointsCargoComplete = pointLURepo.findAllByIds(allCargoComplete.stream().map(Cargo::getId)
                .collect(Collectors.toList()));

        // Убираем дубликаты
        for (PointLUCargo point : allPointsDispatchedCargo) {
            if (!idCargo.equals(point.getCargo().getId())) {
                filteredPointsDispatchedCargo.add(point);
                idCargo = point.getCargo().getId();
            }
        }

        idCargo = 0L;

        for (PointLUCargo point : allPointsActiveCargo) {
            if (!idCargo.equals(point.getCargo().getId())) {
                filteredPointsActiveCargo.add(point);
                idCargo = point.getCargo().getId();
            }
        }

        idCargo = 0L;

        for (PointLUCargo point : allPointsCargoInProcessing) {
            if (!idCargo.equals(point.getCargo().getId())) {
                filteredPointsInProcessingCargo.add(point);
                idCargo = point.getCargo().getId();
            }
        }

        idCargo = 0L;

        for (PointLUCargo point : allPointsCargoComplete) {
            if (!idCargo.equals(point.getCargo().getId())) {
                filteredPointsCompleteCargo.add(point);
                idCargo = point.getCargo().getId();
            }
        }

        cargo.put("allCargoSend", allCargoSend);
        cargo.put("pointsLUDispatchedCargo", filteredPointsDispatchedCargo);
        cargo.put("allCargoActive", allCargoActive);
        cargo.put("pointsLUActiveCargo", filteredPointsActiveCargo);
        cargo.put("allCargoInProcessing", allCargoInProcessing);
        cargo.put("pointsLUInProcessingCargo", filteredPointsInProcessingCargo);
        cargo.put("allCargoComplete", allCargoComplete);
        cargo.put("pointsLUCompleteCargo", filteredPointsCompleteCargo);
    }

    public List<Cargo> getSentOffersCargo(long id) {
        List<CargoOffer> cargoOffers;
        List<Cargo> allCargoSendFrom = new ArrayList<>();

        if (id != 0L) {
            cargoOffers = cargoOfferRepo.findAll();

            // Находим все грузы из заявок, которые отправил юзер
            for (CargoOffer cargoOffer : cargoOffers) {
                // Заявка, которая была отправлена юзером
                if (cargoOffer.getUser() != null) {
                    if (cargoOffer.getUser().getId() == id) {
                        allCargoSendFrom.add(cargoOffer.getCargo());
                    }
                }
            }

            return allCargoSendFrom;
        }

        return null;
    }

    public Cargo changeStatusCargo(Long id) {
        Cargo cargo = cargoRepo.findById(id).get();

        if (cargo.getStatus() == null || cargo.getStatus().equals("")) {
            cargo.setStatus("In processing");
        } else if (cargo.getStatus().equals("In processing")) {
            cargo.setStatus("Loading");
        } else if (cargo.getStatus().equals("Loading")) {
            cargo.setStatus("In way");
        } else if (cargo.getStatus().equals("In way")) {
            cargo.setStatus("Complete");
        }

        cargoRepo.save(cargo);

        return cargo;
    }

    public Map<String, Object> getCountPlaces(List<String> countries) {
        List<PointLUCargo> pointsLUCargo;
        Map<String, Object> pointsInside = new HashMap<>();
        Map<String, Object> pointsFrom = new HashMap<>();
        Map<String, Object> pointsTo = new HashMap<>();

        Map<String, Object> resultMap = new HashMap<>();

        int counterForInside = 0;
        int counterFrom = 0;
        int counterTo = 0;

        for (String country : countries) {
            pointsLUCargo = pointLURepo.getPointsByCountryFromOrCountryTo(country);

            if (pointsLUCargo.size() != 0) {
                System.out.println(pointsLUCargo.size());

                for (PointLUCargo pointLUCargo : pointsLUCargo) {
                    // Если обе страны равны друг другу, то итерируем заявку (внутренние)
                    if (pointLUCargo.getCountryFrom().equals(pointLUCargo.getCountryTo())) {
                        counterForInside++;

                        if (pointsInside.get(pointLUCargo.getCountryFrom()) != null) {
                            pointsInside.replace(pointLUCargo.getCountryFrom(), counterForInside);
                        } else {
                            pointsInside.put(pointLUCargo.getCountryFrom(), counterForInside);
                        }

                    } else {
                        if (country.equals(pointLUCargo.getCountryFrom())) {
                            counterFrom++;

                            if (pointsFrom.get(pointLUCargo.getCountryFrom()) != null) {
                                pointsFrom.replace(pointLUCargo.getCountryFrom(), counterFrom);
                            } else if (pointLUCargo.getCountryFrom() != null) {
                                pointsFrom.put(pointLUCargo.getCountryFrom(), counterFrom);
                            }
                        }

                        if (country.equals(pointLUCargo.getCountryTo())) {
                            counterTo++;

                            if (pointsTo.get(pointLUCargo.getCountryTo()) != null) {
                                pointsTo.replace(pointLUCargo.getCountryTo(), counterTo);
                            } else if (pointLUCargo.getCountryTo() != null) {
                                pointsTo.put(pointLUCargo.getCountryTo(), counterTo);
                            }
                        }
                    }
                }
                counterForInside = 0;
                counterFrom = 0;
                counterTo = 0;
            }
        }

        resultMap.put("pointsInside", pointsInside);
        resultMap.put("pointsFrom", pointsFrom);
        resultMap.put("pointsTo", pointsTo);

        return resultMap;
    }
}
