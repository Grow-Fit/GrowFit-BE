package com.project.growfit.global.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoodApiDto(
    @JsonProperty("foodCd")
    String foodCd,
    @JsonProperty("foodNm")
    String foodNm,

    @JsonProperty("dataCd")
    String dataCd,

    @JsonProperty("typeNm")
    String typeNm,

    @JsonProperty("foodOriginNm")
    String foodOriginNm,

    @JsonProperty("foodLv3Nm")
    String foodLv3Nm,

    @JsonProperty("foodLv4Nm")
    String foodLv4Nm,

    @JsonProperty("foodLv6Nm")
    String foodLv6Nm,

    @JsonProperty("nutConSrtrQua")
    String nutConSrtrQua,

    @JsonProperty("enerc")
    double enerc,

    @JsonProperty("prot")
    double prot,

    @JsonProperty("fatce")
    double fatce,

    @JsonProperty("chocdf")
    double chocdf,

    @JsonProperty("sugar")
    double sugar,

    @JsonProperty("nat")
    double nat,

    @JsonProperty("chole")
    double chole,

    @JsonProperty("mfrNm")
    String mfrNm,

    @JsonProperty("restNm")
    String restNm
) {
}
