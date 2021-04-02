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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CargoService {
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${url.picture.path}")
    private String picturePath;

    @Autowired
    CargoTransportGeneral cargoTransportGeneral;

    private final CargoRepo cargoRepo;
    private final PhotoCargoRepo photoCargoRepo;
    private final PropertyRepo propertyRepo;
    private final PointLUCargoRepo pointLURepo;

    @Autowired
    public CargoService(CargoRepo cargoRepo, PhotoCargoRepo photoCargoRepo, PropertyRepo propertyRepo,
                        PointLUCargoRepo pointLURepo) {
        this.cargoRepo = cargoRepo;
        this.photoCargoRepo = photoCargoRepo;
        this.propertyRepo = propertyRepo;
        this.pointLURepo = pointLURepo;
    }

    public Cargo addCargo(
            String token,
            Cargo cargo,
            PropertiesRequest propertiesCargo,
            List<PointLUCargo> placesCargo,
            String lang,
            MultipartFile firstFile,
            MultipartFile secondFile,
            MultipartFile thirdFile
    ) {
        try {
            return saveCargo(token, cargo, propertiesCargo, placesCargo, lang, firstFile, secondFile,
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
            Cargo cargo,
            PropertiesRequest propertiesCargo,
            List<PointLUCargo> placesCargo,
            String lang,
            MultipartFile firstFile,
            MultipartFile secondFile,
            MultipartFile thirdFile
    ) throws IOException {
        String uuidFile = UUID.randomUUID().toString();
        String resultFileName = "";

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(firstFile);
        multipartFiles.add(secondFile);
        multipartFiles.add(thirdFile);

        if (cargo != null) {
            if (cargoTransportGeneral.setUserLegalUser(token, cargo, null))
                return null;

            addPropertiesCargo(cargo, propertiesCargo, lang);
            cargoRepo.save(cargo);
            addPlacesCargo(cargo, placesCargo);

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

    private void addPlacesCargo(Cargo cargo, List<PointLUCargo> placesCargo) {
        for (PointLUCargo pointLUCargo : placesCargo) {
            if (pointLUCargo.getCityFrom() != null && pointLUCargo.getCountryFrom() != null ||
                    pointLUCargo.getCityTo() != null && pointLUCargo.getCountryTo() != null) {
                pointLUCargo.setCargo(cargo);
                pointLURepo.save(pointLUCargo);
            }
        }
        cargoRepo.save(cargo);
    }

    private void addPropertiesCargo(Cargo cargo, PropertiesRequest propertiesCargo, String lang) {
        if (lang.equals("ru")) {

            if (propertiesCargo.getTypesLoadingTruck() != null) {
                for (String loading : propertiesCargo.getTypesLoadingTruck()) {
                    Property byRuName = propertyRepo.findByRuNameAndProperty(loading, "loading");
                    cargo.getPropertiesCargo().add(byRuName);
                }
            }

            if (propertiesCargo.getTypesUnloadingTruck() != null) {
                for (String unloading : propertiesCargo.getTypesUnloadingTruck()) {
                    Property byRuName = propertyRepo.findByRuNameAndProperty(unloading, "unloading");
                    cargo.getPropertiesCargo().add(byRuName);
                }
            }

            if (propertiesCargo.getPermissions() != null) {
                for (String permission : propertiesCargo.getPermissions()) {
                    Property byRuName = propertyRepo.findByRuName(permission);
                    cargo.getPropertiesCargo().add(byRuName);
                }
            }

            if (propertiesCargo.getTypePayment() != null && !propertiesCargo.getTypePayment().equals("")) {
                Property byRuName = propertyRepo.findByRuName(propertiesCargo.getTypePayment());
                cargo.getPropertiesCargo().add(byRuName);
            }

            if (propertiesCargo.getCostPer() != null && !propertiesCargo.getCostPer().equals("")) {
                Property byRuName = propertyRepo.findByRuName(propertiesCargo.getCostPer());
                cargo.getPropertiesCargo().add(byRuName);
            }

            if (propertiesCargo.getPaymentForm() != null && !propertiesCargo.getPaymentForm().equals("")) {
                Property byRuName = propertyRepo.findByRuName(propertiesCargo.getPaymentForm());
                cargo.getPropertiesCargo().add(byRuName);
            }

            if (propertiesCargo.getPaymentTime() != null && !propertiesCargo.getPaymentTime().equals("")) {
                Property byRuName = propertyRepo.findByRuName(propertiesCargo.getPaymentTime());
                cargo.getPropertiesCargo().add(byRuName);
            }
        }
    }

    public Map<String, Object> searchCargo(CargoRequest cargoRequest, int page, int pageSize) {
        List<Cargo> cargo;
        Set<Long> tempCargoId;
        List<Long> resultId;

        Page<Cargo> pageCargo;
        Map<String, Object> cargoMap = new HashMap<>();

        Pageable pageable = PageRequest.of(page, pageSize);

        replacePlusOnSpace(cargoRequest);

        // Находим совпадение по странам и городам
        tempCargoId = cargoRepo.getCargoIds(cargoRequest.getCountryFrom(), cargoRequest.getCityFrom(),
                cargoRequest.getCountryTo(), cargoRequest.getCityTo());

        if (cargoRequest.getLoadingDateFrom() == null && cargoRequest.getLoadingDateBy() == null) {
            pageCargo = cargoRepo.searchCargoWithParams(tempCargoId, cargoRequest.getWeightFrom(),
                    cargoRequest.getWeightUpTo(), cargoRequest.getVolumeFrom(), cargoRequest.getVolumeUpTo(),
                    cargoRequest.getNameCargo(), cargoRequest.getBodyType(), pageable);

            cargo = pageCargo.getContent();

            // Возврат всех мест загрузки и разгрузки груза
            resultId = getIdCargoPlaces(cargo);
        } else {
            pageCargo = getCargoByDate(cargoRequest, tempCargoId, pageable);

            cargo = pageCargo.getContent();

            // Возврат всех мест загрузки и разгрузки груза
            resultId = getIdCargoPlaces(cargo);

            if (cargo.isEmpty()) {
                cargo = new ArrayList<>();
                cargoMap.put("cargo", cargo);
                return cargoMap;
            }

            if (cargoRequest.getPaymentForm() == null && cargoRequest.getPaymentTime() == null) {
                System.out.println("FORM = 0 & TIME = 0");
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

    private Page<Cargo> getCargoByDate(CargoRequest cargoRequest, Set<Long> tempCargoId, Pageable pageable) {
        if (cargoRequest.getLoadingDateFrom() != null && cargoRequest.getLoadingDateBy() == null) {
            return cargoRepo.findByLoadingDateFrom(tempCargoId, cargoRequest.getLoadingDateFrom(),
                    cargoRequest.getWeightFrom(), cargoRequest.getWeightUpTo(), cargoRequest.getVolumeFrom(),
                    cargoRequest.getVolumeUpTo(), cargoRequest.getNameCargo(), cargoRequest.getBodyType(),
                    pageable);
        } else if (cargoRequest.getLoadingDateBy() != null && cargoRequest.getLoadingDateFrom() == null) {
            return cargoRepo.findByLoadingDateBy(tempCargoId, cargoRequest.getLoadingDateBy(),
                    cargoRequest.getWeightFrom(), cargoRequest.getWeightUpTo(), cargoRequest.getVolumeFrom(),
                    cargoRequest.getVolumeUpTo(), cargoRequest.getNameCargo(), cargoRequest.getBodyType(),
                    pageable);
        } else {
            return cargoRepo.getAllBetweenTwoDate(tempCargoId, cargoRequest.getLoadingDateFrom(),
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
                for (Property property : item.getPropertiesCargo()) {
                    if (property.getRuName().equals(cargoRequest.getPaymentForm())) {
                        paymentFormList.add(item);
                    }
                }
            }
            // Если заполнено только поле момента оплаты
        } else if (cargoRequest.getPaymentTime() != null && cargoRequest.getPaymentForm() == null) {
            // Заполнение грузов при заполненном моменте оплаты и с введённой датой
            for (Cargo item : cargo) {
                for (Property property : item.getPropertiesCargo()) {
                    if (property.getRuName().equals(cargoRequest.getPaymentTime())) {
                        paymentTimeList.add(item);
                    }
                }
            }
            // Если заполнены оба поля
        } else {
            System.out.println("ДАТА ЗАПОЛНЕНА");
            for (Cargo item : cargo) {
                for (Property property : item.getPropertiesCargo()) {
                    if (property.getRuName().equals(cargoRequest.getPaymentForm())) {
                        paymentFormList.add(item);
                    }
                }
            }

            for (Cargo item : cargo) {
                for (Property property : item.getPropertiesCargo()) {
                    if (property.getRuName().equals(cargoRequest.getPaymentTime())) {
                        paymentTimeList.add(item);
                    }
                }
            }

            if (paymentFormList.isEmpty() || paymentTimeList.isEmpty()) {
                cargo = new ArrayList<>();
            }

            if (!paymentFormList.isEmpty() && !paymentTimeList.isEmpty()) {
                boolean checkFilledFormAndTimeList = true;
                for (Cargo item : paymentFormList) {
                    for (Cargo value : paymentTimeList) {
                        if (item.getId().equals(value.getId())) {
                            if (checkFilledFormAndTimeList) {
                                cargo.clear();
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

    public Cargo getCargo(long id) {
        return cargoRepo.findById(id);
    }

    public List<PointLUCargo> getPointsCargo(long id) {
        return pointLURepo.getPointsLUCargoById(id);
    }

    public List<PhotoCargo> getPhotoCargo(long id) {
        return photoCargoRepo.findByCargo_Id(id);
    }

    public Integer getCountCargo(long id, String role) {
        System.out.println(id);
        System.out.println(role);
        if (role.equals("ROLE_USER")) {
            List<Cargo> cargo = cargoRepo.findByUser_Id(id);
            return cargo.size();
        }

        if (role.equals("ROLE_LEGAL_USER")) {
            List<Cargo> cargo = cargoRepo.findByLegalUser_Id(id);
            return cargo.size();
        }

        return 0;
    }
}
