package com.steshkovladyslav.transportexchangebackend.service;

import com.steshkovladyslav.transportexchangebackend.model.*;
import com.steshkovladyslav.transportexchangebackend.payload.request.PropertiesRequest;
import com.steshkovladyslav.transportexchangebackend.payload.request.transport.TransportRequest;
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
public class TransportService {
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${url.picture.path}")
    private String picturePath;

    @Autowired
    CargoTransportGeneral cargoTransportGeneral;

    private final TransportRepo transportRepo;
    private final PhotoTransportRepo photoTransportRepo;
    private final PropertyRepo propertyRepo;
    private final PointLUTransRepo pointLUTransRepo;

    @Autowired
    public TransportService(TransportRepo transportRepo, PhotoTransportRepo photoTransportRepo, PropertyRepo propertyRepo,
                            PointLUTransRepo pointLUTransRepo) {
        this.transportRepo = transportRepo;
        this.photoTransportRepo = photoTransportRepo;
        this.propertyRepo = propertyRepo;
        this.pointLUTransRepo = pointLUTransRepo;
    }

    public List<Transport> getTransports() {
        return transportRepo.findAll();
    }

    public Transport addTransport(
            String token,
            Transport transport,
            List<PointLUTransport> placesTransport,
            PropertiesRequest propertiesTransport,
            String lang,
            MultipartFile firstFile,
            MultipartFile secondFile,
            MultipartFile thirdFile) {
        try {
            return saveTransport(token, transport, placesTransport, propertiesTransport, lang, firstFile,
                    secondFile, thirdFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Transport saveTransport(
            String token,
            Transport transport,
            List<PointLUTransport> placesTransport,
            PropertiesRequest propertiesTransport,
            String lang,
            MultipartFile firstFile,
            MultipartFile secondFile,
            MultipartFile thirdFile) throws IOException {
        String uuidFile = UUID.randomUUID().toString();
        String resultFileName = "";

        List<MultipartFile> multipartFiles = new ArrayList<>();

        multipartFiles.add(firstFile);
        multipartFiles.add(secondFile);
        multipartFiles.add(thirdFile);

        if (transport != null) {
            if (cargoTransportGeneral.setUserLegalUser(token, null, transport))
                return null;

            addPropertiesTransport(transport, propertiesTransport, lang);
            transportRepo.save(transport);
            addPlacesTransport(transport, placesTransport);

            if (firstFile != null || secondFile != null || thirdFile != null) {
                File uploadDir = new File(uploadPath);

                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
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
        }
        return null;
    }

    private void addPlacesTransport(Transport transport, List<PointLUTransport> placesTransport) {
        for (PointLUTransport pointLUTransport : placesTransport) {
            if (pointLUTransport.getCityFrom() != null && pointLUTransport.getCountryFrom() != null ||
                    pointLUTransport.getCityTo() != null && pointLUTransport.getCountryTo() != null) {
                pointLUTransport.setTransport(transport);
                pointLUTransRepo.save(pointLUTransport);
            }
        }
        transportRepo.save(transport);
    }

    private void addPropertiesTransport(Transport transport, PropertiesRequest propertiesTransport, String lang) {
        if (lang.equals("ru")) {
            if (propertiesTransport.getTypesLoadingTruck() != null) {
                for (String loading : propertiesTransport.getTypesLoadingTruck()) {
                    Property byRuName = propertyRepo.findByRuNameAndProperty(loading, "loading");
                    transport.getPropertiesTransport().add(byRuName);
                }
            }

            if (propertiesTransport.getTypesUnloadingTruck() != null) {
                for (String unloading : propertiesTransport.getTypesUnloadingTruck()) {
                    Property byRuName = propertyRepo.findByRuNameAndProperty(unloading, "unloading");
                    transport.getPropertiesTransport().add(byRuName);
                }
            }

            if (propertiesTransport.getPermissions() != null) {
                for (String permission : propertiesTransport.getPermissions()) {
                    Property byRuName = propertyRepo.findByRuName(permission);
                    transport.getPropertiesTransport().add(byRuName);
                }
            }

            if (propertiesTransport.getTypePayment() != null && !propertiesTransport.getTypePayment().equals("")) {
                Property byRuName = propertyRepo.findByRuName(propertiesTransport.getTypePayment());
                transport.getPropertiesTransport().add(byRuName);
            }

            if (propertiesTransport.getCostPer() != null && !propertiesTransport.getCostPer().equals("")) {
                Property byRuName = propertyRepo.findByRuName(propertiesTransport.getCostPer());
                transport.getPropertiesTransport().add(byRuName);
            }

            if (propertiesTransport.getPaymentForm() != null && !propertiesTransport.getPaymentForm().equals("")) {
                Property byRuName = propertyRepo.findByRuName(propertiesTransport.getPaymentForm());
                transport.getPropertiesTransport().add(byRuName);
            }

            if (propertiesTransport.getPaymentTime() != null && !propertiesTransport.getPaymentTime().equals("")) {
                Property byRuName = propertyRepo.findByRuName(propertiesTransport.getPaymentTime());
                transport.getPropertiesTransport().add(byRuName);
            }
        }
    }

    public Map<String, Object> searchTransport(TransportRequest transportRequest, int page, int pageSize) {
        List<Transport> transports;
        Set<Long> tempCargoId;
        List<Long> resultId;

        Page<Transport> transportPage;
        Map<String, Object> transportMap = new HashMap<>();

        Pageable pageable = PageRequest.of(page, pageSize);

        replacePlusOnSpace(transportRequest);

        // Находим совпадение по странам и городам
        tempCargoId = transportRepo.getTransportIds(transportRequest.getCountryFrom(), transportRequest.getCityFrom(),
                transportRequest.getCountryTo(), transportRequest.getCityTo());

        if (transportRequest.getLoadingDateFrom() == null && transportRequest.getLoadingDateBy() == null) {
            transportPage = transportRepo.searchTransportsWithParams(tempCargoId,
                    transportRequest.getCarryingCapacityFrom(), transportRequest.getCarryingCapacityUpTo(),
                    transportRequest.getVolumeFrom(), transportRequest.getVolumeUpTo(), transportRequest.getBodyType(),
                    pageable);

            transports = transportPage.getContent();

            // Возврат всех мест загрузки и разгрузки груза
            resultId = getIdTransportsPlaces(transports);
        } else {
            transportPage = getTransportsByDate(transportRequest, tempCargoId, pageable);

            transports = transportPage.getContent();

            // Возврат всех мест загрузки и разгрузки груза
            resultId = getIdTransportsPlaces(transports);

            if (transports.isEmpty()) {
                transports = new ArrayList<>();
                transportMap.put("transports", transports);
                return transportMap;
            }

            if (transportRequest.getPaymentForm() == null && transportRequest.getPaymentTime() == null) {
                System.out.println("FORM = 0 & TIME = 0");
                filledTransportMap(transportMap, transports, pointLUTransRepo.findAllByIds(resultId), transportPage);
                return transportMap;
            }
        }

        if (transportRequest.getPaymentForm() != null || transportRequest.getPaymentTime() != null) {
            transports = checkPaymentForm(transportRequest, transports);


            if (transports != null) {
                // Возврат всех мест загрузки и разгрузки груза
                resultId = getIdTransportsPlaces(transports);

                filledTransportMap(transportMap, transports, pointLUTransRepo.findAllByIds(resultId), transportPage);

                return transportMap;
            } else {
                transports = new ArrayList<>();
                transportMap.put("transports", transports);
                return transportMap;
            }
        }

        filledTransportMap(transportMap, transports, pointLUTransRepo.findAllByIds(resultId), transportPage);

        return transportMap;
    }

    private List<Long> getIdTransportsPlaces(List<Transport> transports) {
        return transports.stream().map(Transport::getId).collect(Collectors.toList());
    }

    private Page<Transport> getTransportsByDate(TransportRequest transportRequest, Set<Long> tempCargoId, Pageable pageable) {
        if (transportRequest.getLoadingDateFrom() != null && transportRequest.getLoadingDateBy() == null) {
            return transportRepo.findByLoadingDateFrom(tempCargoId, transportRequest.getLoadingDateFrom(),
                    transportRequest.getCarryingCapacityFrom(), transportRequest.getCarryingCapacityUpTo(),
                    transportRequest.getVolumeFrom(), transportRequest.getVolumeUpTo(), transportRequest.getBodyType(),
                    pageable);
        } else if (transportRequest.getLoadingDateBy() != null && transportRequest.getLoadingDateFrom() == null) {
            return transportRepo.findByLoadingDateBy(tempCargoId, transportRequest.getLoadingDateBy(),
                    transportRequest.getCarryingCapacityFrom(), transportRequest.getCarryingCapacityUpTo(),
                    transportRequest.getVolumeFrom(), transportRequest.getVolumeUpTo(), transportRequest.getBodyType(),
                    pageable);
        } else {
            return transportRepo.getAllBetweenTwoDate(tempCargoId, transportRequest.getLoadingDateFrom(),
                    transportRequest.getLoadingDateBy(), transportRequest.getCarryingCapacityFrom(),
                    transportRequest.getCarryingCapacityUpTo(), transportRequest.getVolumeFrom(),
                    transportRequest.getVolumeUpTo(), transportRequest.getBodyType(), pageable);
        }
    }

    private List<Transport> checkPaymentForm(TransportRequest transportRequest, List<Transport> transports) {
        List<Transport> paymentFormList = new ArrayList<>();
        List<Transport> paymentTimeList = new ArrayList<>();

        // Если заполнена только форма оплаты
        if (transportRequest.getPaymentForm() != null && transportRequest.getPaymentTime() == null) {

            if (transportRequest.getPaymentForm() != null && transportRequest.getPaymentTime() == null) {
                for (Transport item : transports) {
                    for (Property property : item.getPropertiesTransport()) {
                        if (property.getRuName().equals(transportRequest.getPaymentForm())) {
                            paymentFormList.add(item);
                        }
                    }
                }
            }
            // Если заполнено только поле момента оплаты
        } else if (transportRequest.getPaymentTime() != null && transportRequest.getPaymentForm() == null) {
            // Заполнение грузов при заполненном моменте оплаты и с введённой датой
            if (transportRequest.getPaymentTime() != null && transportRequest.getPaymentForm() == null) {
                for (Transport item : transports) {
                    for (Property property : item.getPropertiesTransport()) {
                        if (property.getRuName().equals(transportRequest.getPaymentTime())) {
                            paymentTimeList.add(item);
                        }
                    }
                }
            }
            // Если заполнены оба поля
        } else {
            System.out.println("ДАТА ЗАПОЛНЕНА");
            for (Transport item : transports) {
                for (Property property : item.getPropertiesTransport()) {
                    if (property.getRuName().equals(transportRequest.getPaymentForm())) {
                        paymentFormList.add(item);
                    }
                }
            }

            for (Transport item : transports) {
                for (Property property : item.getPropertiesTransport()) {
                    if (property.getRuName().equals(transportRequest.getPaymentTime())) {
                        paymentTimeList.add(item);
                    }
                }
            }

            if (paymentFormList.isEmpty() || paymentTimeList.isEmpty()) {
                transports = new ArrayList<>();
            }

            if (!paymentFormList.isEmpty() && !paymentTimeList.isEmpty()) {
                boolean checkFilledFormAndTimeList = true;
                for (Transport item : paymentFormList) {
                    for (Transport value : paymentTimeList) {
                        if (item.getId().equals(value.getId())) {
                            if (checkFilledFormAndTimeList) {
                                transports.clear();
                                checkFilledFormAndTimeList = false;
                            }
                            transports.add(item);
                        }
                    }
                }
            }

            paymentFormList.clear();
            paymentTimeList.clear();
        }

        if (!paymentFormList.isEmpty()) {
            transports = new ArrayList<>(paymentFormList);
        }
        if (!paymentTimeList.isEmpty()) {
            transports = new ArrayList<>(paymentTimeList);
        }


        return transports;
    }

    private void replacePlusOnSpace(TransportRequest transportRequest) {
        if (transportRequest.getCountryFrom() != null)
            transportRequest.setCountryFrom(transportRequest.getCountryFrom().replaceAll("\\+", " "));

        if (transportRequest.getCountryTo() != null)
            transportRequest.setCountryTo(transportRequest.getCountryTo().replaceAll("\\+", " "));

        if (transportRequest.getCityFrom() != null)
            transportRequest.setCityFrom(transportRequest.getCityFrom().replaceAll("\\+", " "));

        if (transportRequest.getCityTo() != null)
            transportRequest.setCityTo(transportRequest.getCityTo().replaceAll("\\+", " "));

        if (transportRequest.getBodyType() != null)
            transportRequest.setBodyType(transportRequest.getBodyType().replaceAll("\\+", " "));
    }

    private void filledTransportMap(Map<String, Object> transportMap,
                                    List<Transport> transports,
                                    List<PointLUTransport> pointsLUTransports,
                                    Page<Transport> transportPage) {
        transportMap.put("transports", transports);
        transportMap.put("pointsLUTransports", pointsLUTransports);
        transportMap.put("currentPage", transportPage.getNumber());
        transportMap.put("totalPages", transportPage.getTotalPages());
    }

    public Transport getTransport(long id) {
        return transportRepo.findById(id);
    }

    public List<PointLUTransport> getPointsTransport(long id) {
        return pointLUTransRepo.getPointsLUTransportById(id);
    }

    public List<PhotoTransport> getPhotoTransport(long id) {
        return photoTransportRepo.findByTransport_Id(id);
    }

    public Integer getCountTransport(long id, String role) {
        if (role.equals("ROLE_USER")) {
            List<Transport> transports = transportRepo.findByUser_Id(id);
            for(Transport transport : transports){
                System.out.println(transport.getId());
            }
            return transports.size();
        }

        if (role.equals("ROLE_LEGAL_USER")) {
            List<Transport> transports = transportRepo.findByLegalUser_Id(id);
            for(Transport transport : transports){
                System.out.println(transport.getId());
            }
            return transports.size();
        }

        return 0;
    }
}
