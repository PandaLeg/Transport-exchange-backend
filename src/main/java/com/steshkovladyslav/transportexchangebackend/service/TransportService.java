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
import org.springframework.http.ResponseEntity;
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

    private final UserRepo userRepo;

    private final TransportRepo transportRepo;
    private final PhotoTransportRepo photoTransportRepo;
    private final PropertyRepo propertyRepo;
    private final PointLUTransRepo pointLUTransRepo;
    private final TransportOfferRepo transportOfferRepo;

    @Autowired
    public TransportService(UserRepo userRepo, TransportRepo transportRepo, PhotoTransportRepo photoTransportRepo,
                            PropertyRepo propertyRepo, PointLUTransRepo pointLUTransRepo,
                            TransportOfferRepo transportOfferRepo) {
        this.userRepo = userRepo;
        this.transportRepo = transportRepo;
        this.photoTransportRepo = photoTransportRepo;
        this.propertyRepo = propertyRepo;
        this.pointLUTransRepo = pointLUTransRepo;
        this.transportOfferRepo = transportOfferRepo;
    }

    public List<Transport> getTransports() {
        return transportRepo.findAll();
    }

    public Transport addTransport(
            String token,
            Transport transport,
            List<PointLUTransport> placesTransport,
            PropertiesRequest propertiesTransport,
            MultipartFile firstFile,
            MultipartFile secondFile,
            MultipartFile thirdFile) {
        try {
            return saveTransport(token, transport, placesTransport, propertiesTransport, firstFile,
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
            if (!cargoTransportGeneral.setUser(token, null, transport))
                return null;

            addPropertiesTransport(transport, propertiesTransport);
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
            return transport;
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

    private void addPropertiesTransport(Transport transport, PropertiesRequest propertiesTransport) {
        if (propertiesTransport.getTypesLoadingTruck() != null) {
            for (String loading : propertiesTransport.getTypesLoadingTruck()) {
                System.out.println(loading);
                Property byRuName = propertyRepo.findByNameAndProperty(loading, "loading");
                transport.getPropertiesTransport().add(byRuName);
            }
        }

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
            for (Transport item : transports) {
                Property property = getProperty(item, "paymentForm");

                if (property != null) {
                    if (property.getRuName().equals(transportRequest.getPaymentForm()) ||
                            property.getEnName().equals(transportRequest.getPaymentForm()) ||
                            property.getUaName().equals(transportRequest.getPaymentForm())) {
                        paymentFormList.add(item);
                    }
                }
            }

            if (paymentFormList.isEmpty()) {
                transports = null;
            }
            // Если заполнено только поле момента оплаты
        } else if (transportRequest.getPaymentTime() != null && transportRequest.getPaymentForm() == null) {
            for (Transport item : transports) {
                Property property = getProperty(item, "paymentTime");

                if (property != null) {
                    if (property.getRuName().equals(transportRequest.getPaymentTime()) ||
                            property.getEnName().equals(transportRequest.getPaymentTime()) ||
                            property.getUaName().equals(transportRequest.getPaymentTime())) {
                        paymentTimeList.add(item);
                    }
                }
            }

            if (paymentTimeList.isEmpty()) {
                transports = null;
            }
            // Если заполнены оба поля
        } else {
            System.out.println("ДАТА ЗАПОЛНЕНА");
            for (Transport item : transports) {
                Property property = getProperty(item, "paymentForm");

                if (property != null) {
                    if (property.getRuName().equals(transportRequest.getPaymentForm()) ||
                            property.getEnName().equals(transportRequest.getPaymentForm()) ||
                            property.getUaName().equals(transportRequest.getPaymentForm())) {
                        paymentFormList.add(item);
                    }
                }
            }

            for (Transport item : transports) {
                Property property = getProperty(item, "paymentTime");

                if (property != null) {
                    if (property.getRuName().equals(transportRequest.getPaymentTime()) ||
                            property.getEnName().equals(transportRequest.getPaymentTime()) ||
                            property.getUaName().equals(transportRequest.getPaymentTime())) {
                        paymentTimeList.add(item);
                    }
                }
            }

            if (paymentFormList.isEmpty() || paymentTimeList.isEmpty()) {
                transports = null;
            }

            if (!paymentFormList.isEmpty() && !paymentTimeList.isEmpty()) {
                boolean checkFilledFormAndTimeList = true;
                for (Transport item : paymentFormList) {
                    for (Transport value : paymentTimeList) {
                        if (item.getId().equals(value.getId())) {
                            if (checkFilledFormAndTimeList) {
                                transports = new ArrayList<>();
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

    private Property getProperty(Transport transport, String payment) {
        return transport.getPropertiesTransport().stream()
                .filter(i -> payment.equals(i.getProperty()))
                .findFirst().orElse(null);
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

    public Map<String, Object> getTransport(long id) {
        Map<String, Object> transports = new HashMap<>();

        Transport transport = transportRepo.findById(id);
        List<PointLUTransport> pointsLUCargoById = pointLUTransRepo.getPointsLUTransportById(id);

        User user = userRepo.findById((long) transport.getUser().getId());

        transports.put("user", user);
        transports.put("transport", transport);
        transports.put("pointsLUTransport", pointsLUCargoById);

        return transports;
    }

    public List<PointLUTransport> getPointsTransport(long id) {
        return pointLUTransRepo.getPointsLUTransportById(id);
    }

    public List<PhotoTransport> getPhotoTransport(long id) {
        return photoTransportRepo.findByTransport_Id(id);
    }

    public Integer getCountTransport(long id, String role) {
        List<Transport> transports = transportRepo.findAllByUser_Id(id);
        return transports.size();
    }

    public ResponseEntity<?> addTransportOffer(long idTransport, TransportOffer transportOffer, String role,
                                               long idUser) {
        Transport transport;

        if (idTransport != 0) {
            transport = transportRepo.findById(idTransport);
            transportOffer.setTransport(transport);

            User user = userRepo.findById(idUser);

            if (user != null) {
                transportOffer.setUser(user);
                transportOfferRepo.save(transportOffer);
            } else {
                return null;
            }

            return ResponseEntity.ok("OK");
        }

        return (ResponseEntity<?>) ResponseEntity.noContent();
    }

    public Map<String, Object> getAllOfferTransports(long id, String role) {
        Map<String, Object> transports = new HashMap<>();
        List<PointLUTransport> filteredArray = new ArrayList<>();
        List<PointLUTransport> allByIds;

        Long idTransport = 0L;

        if (id != 0L) {
            List<Transport> allByUser_id = transportRepo.findAllByUser_Id(id);
            allByIds = pointLUTransRepo.findAllByIds(allByUser_id.stream().map(Transport::getId)
                    .collect(Collectors.toList()));

            // Убираем дубликаты
            for (PointLUTransport point : allByIds) {
                if (!idTransport.equals(point.getTransport().getId())) {
                    filteredArray.add(point);
                    idTransport = point.getTransport().getId();
                }
            }

            transports.put("transports", allByUser_id);
            transports.put("pointsLUTransports", filteredArray);
            return transports;
        }

        return null;
    }

    public Map<String, Object> getActiveAndSentOffersTransports(long id, String role) {
        Map<String, Object> transports = new HashMap<>();
        List<TransportOffer> transportOffers;
        List<Transport> transportsFromOffers;

        List<Transport> transportsSend = new ArrayList<>();
        List<Transport> transportsActive = new ArrayList<>();
        List<Transport> transportsInProcessing = new ArrayList<>();
        List<Transport> transportsComplete = new ArrayList<>();

        List<PointLUTransport> filteredPointsDispatchedTransport = new ArrayList<>();
        List<PointLUTransport> filteredPointsActiveTransport = new ArrayList<>();
        List<PointLUTransport> filteredPointsTransportInProcessing = new ArrayList<>();
        List<PointLUTransport> filteredPointsCompleteTransport = new ArrayList<>();

        Long idTransport = 0L;


        if (id != 0L) {
            transportOffers = transportOfferRepo.findAll();
            transportsFromOffers = transportRepo.getByTransportId();

            for (TransportOffer transportOffer : transportOffers) {
                // Заявка, которая была отправлена юзером
                if (transportOffer.getUser() != null) {
                    if (transportOffer.getUser().getId() == id) {
                        if (transportOffer.getTransport().getStatus() != null &&
                                !transportOffer.getTransport().getStatus().equals("Complete")) {
                            transportsInProcessing.add(transportOffer.getTransport());
                        } else if (transportOffer.getTransport().getStatus() != null &&
                                transportOffer.getTransport().getStatus().equals("Complete")) {
                            transportsComplete.add(transportOffer.getTransport());
                        } else {
                            transportsSend.add(transportOffer.getTransport());
                        }
                    }
                }
            }

            for (Transport t : transportsFromOffers) {
                // Заявка которую отправили юзеру
                if (t.getUser() != null) {
                    if (t.getUser().getId() == id) {
                        if (t.getStatus() != null && !t.getStatus().equals("Complete")) {
                            transportsInProcessing.add(t);
                        } else if (t.getStatus() != null && t.getStatus().equals("Complete")) {
                            transportsComplete.add(t);
                        } else {
                            transportsActive.add(t);
                        }
                    }
                }
            }

            setPointsTransportsAndFilledMap(transports, transportsSend, transportsActive, transportsInProcessing,
                    transportsComplete, filteredPointsDispatchedTransport, filteredPointsActiveTransport,
                    filteredPointsTransportInProcessing, filteredPointsCompleteTransport, idTransport);

            return transports;
        }

        return null;
    }

    private void setPointsTransportsAndFilledMap(Map<String, Object> transports, List<Transport> transportsSend,
                                                 List<Transport> transportsActive, List<Transport> transportsInProcessing,
                                                 List<Transport> transportsComplete,
                                                 List<PointLUTransport> filteredPointsDispatchedTransport,
                                                 List<PointLUTransport> filteredPointsActiveTransport,
                                                 List<PointLUTransport> filteredPointsTransportInProcessing,
                                                 List<PointLUTransport> filteredPointsCompleteTransport, Long idTransport) {
        List<PointLUTransport> allPointsDispatchedTransport;
        List<PointLUTransport> allPointsActiveTransport;
        List<PointLUTransport> allPointsInProcessingTransport;
        List<PointLUTransport> allPointsCompleteTransport;

        allPointsDispatchedTransport = pointLUTransRepo.findAllByIds(transportsSend.stream().map(Transport::getId)
                .collect(Collectors.toList()));

        allPointsActiveTransport = pointLUTransRepo.findAllByIds(transportsActive.stream().map(Transport::getId)
                .collect(Collectors.toList()));

        allPointsInProcessingTransport = pointLUTransRepo.findAllByIds(transportsInProcessing.stream().map(Transport::getId)
                .collect(Collectors.toList()));

        allPointsCompleteTransport = pointLUTransRepo.findAllByIds(transportsComplete.stream().map(Transport::getId)
                .collect(Collectors.toList()));

        // Убираем дубликаты
        for (PointLUTransport point : allPointsDispatchedTransport) {
            if (!idTransport.equals(point.getTransport().getId())) {
                filteredPointsDispatchedTransport.add(point);
                idTransport = point.getTransport().getId();
            }
        }

        if (idTransport != 0L) {
            idTransport = 0L;
        }

        for (PointLUTransport point : allPointsActiveTransport) {
            if (!idTransport.equals(point.getTransport().getId())) {
                filteredPointsActiveTransport.add(point);
                idTransport = point.getTransport().getId();
            }
        }

        if (idTransport != 0L) {
            idTransport = 0L;
        }

        for (PointLUTransport point : allPointsInProcessingTransport) {
            if (!idTransport.equals(point.getTransport().getId())) {
                filteredPointsTransportInProcessing.add(point);
                idTransport = point.getTransport().getId();
            }
        }

        if (idTransport != 0L) {
            idTransport = 0L;
        }

        for (PointLUTransport point : allPointsCompleteTransport) {
            if (!idTransport.equals(point.getTransport().getId())) {
                filteredPointsCompleteTransport.add(point);
                idTransport = point.getTransport().getId();
            }
        }

        transports.put("transportsSend", transportsSend);
        transports.put("pointsLUDispatchedTransport", filteredPointsDispatchedTransport);
        transports.put("transportsActive", transportsActive);
        transports.put("pointsLUActiveTransport", filteredPointsActiveTransport);
        transports.put("transportsInProcessing", transportsInProcessing);
        transports.put("pointsLUTransportInProcessing", filteredPointsTransportInProcessing);
        transports.put("transportsComplete", transportsComplete);
        transports.put("pointsLUTransportComplete", filteredPointsCompleteTransport);
    }

    public List<Transport> getSentOffersTransports(long id, String role) {
        List<TransportOffer> transportOffers;
        List<Transport> transportsSendFrom = new ArrayList<>();

        if (id != 0L) {
            transportOffers = transportOfferRepo.findAll();

            // Находим все грузы из заявок, которые отправил юзер
            for (TransportOffer transportOffer : transportOffers) {
                // Заявка, которая была отправлена юзером
                if (transportOffer.getUser() != null) {
                    if (transportOffer.getUser().getId() == id) {
                        transportsSendFrom.add(transportOffer.getTransport());
                    }
                }
            }

            return transportsSendFrom;
        }

        return null;
    }

    public Transport changeStatusTransport(Long id) {
        Transport transport = transportRepo.findById(id).get();

        if (transport.getStatus() == null || transport.getStatus().equals("")) {
            transport.setStatus("In processing");
        } else if (transport.getStatus().equals("In processing")) {
            transport.setStatus("Loading");
        } else if (transport.getStatus().equals("Loading")) {
            transport.setStatus("In way");
        } else if (transport.getStatus().equals("In way")) {
            transport.setStatus("Complete");
        }

        transportRepo.save(transport);

        return transport;
    }

    public Map<String, Object> getCountPlaces(List<String> countries) {
        List<PointLUTransport> pointLUTransports;
        Map<String, Object> pointsInside = new HashMap<>();
        Map<String, Object> pointsFrom = new HashMap<>();
        Map<String, Object> pointsTo = new HashMap<>();

        Map<String, Object> resultMap = new HashMap<>();

        int counterForInside = 0;
        int counterFrom = 0;
        int counterTo = 0;

        for (String country : countries) {
            pointLUTransports = pointLUTransRepo.getPointsByCountryFromOrCountryTo(country);

            if (pointLUTransports.size() != 0) {
                for (PointLUTransport pointLUTransport : pointLUTransports) {
                    // Если обе страны равны друг другу, то итерируем заявку (внутренние)
                    if (pointLUTransport.getCountryFrom().equals(pointLUTransport.getCountryTo())) {
                        counterForInside++;

                        if (pointsInside.get(pointLUTransport.getCountryFrom()) != null) {
                            pointsInside.replace(pointLUTransport.getCountryFrom(), counterForInside);
                        } else {
                            pointsInside.put(pointLUTransport.getCountryFrom(), counterForInside);
                        }

                    } else {
                        if (country.equals(pointLUTransport.getCountryFrom())) {
                            counterFrom++;

                            if (pointsFrom.get(pointLUTransport.getCountryFrom()) != null) {
                                pointsFrom.replace(pointLUTransport.getCountryFrom(), counterFrom);
                            } else if (pointLUTransport.getCountryFrom() != null) {
                                pointsFrom.put(pointLUTransport.getCountryFrom(), counterFrom);
                            }
                        }

                        if (country.equals(pointLUTransport.getCountryTo())) {
                            counterTo++;

                            if (pointsTo.get(pointLUTransport.getCountryTo()) != null) {
                                pointsTo.replace(pointLUTransport.getCountryTo(), counterTo);
                            } else if (pointLUTransport.getCountryTo() != null) {
                                pointsTo.put(pointLUTransport.getCountryTo(), counterTo);
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
