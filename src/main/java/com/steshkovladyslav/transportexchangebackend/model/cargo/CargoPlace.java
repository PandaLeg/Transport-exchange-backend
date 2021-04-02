package com.steshkovladyslav.transportexchangebackend.model.cargo;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CargoPlace {
    Long getId();
    String getName();
    String getWeightFrom();
    String getWeightUpTo();
    String getVolumeFrom();
    String getVolumeUpTo();
    String getLengthCargo();
    String getWidthCargo();
    String getHeightCargo();
    String getAdr();
    LocalDate getLoadingDateFrom();
    LocalDate getLoadingDateBy();
    String getBodyType();
    String getCost();
    String getCurrency();
    String getPrepayment();
    String getAdditional();
    String get_City_From();
    String get_Country_From();
    String get_City_To();
    String get_Country_To();
    BigDecimal getLatFirstPoint();
    BigDecimal getLngFirstPoint();
    BigDecimal getLatSecondPoint();
    BigDecimal getLngSecondPoint();
}
