package com.project.growfit.global.api.entity;

import com.project.growfit.global.api.dto.FoodApiDto;
import com.project.growfit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@Getter
@Table(schema = "food_data")
public class FoodApi extends BaseEntity {

    @Id
    @Column(name = "food_data_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long food_data;

    /* 식품 코드 */
    @Column(name = "food_data_cd", nullable = false)
    private String foodCd;

    /* 삭품명 */
    @Column(name = "food_data_name", nullable = false)
    private String foodNm;

    /* 데이터 구분코드 */
    @Column(name = "data_cd", nullable = false)
    private String dataCd;

    /* 데이터 구분명 */
    @Column(name = "food_data_type_name", nullable = false)
    private String typeNm;

    /* 식품기원명 */
    @Column(name = "food_data_origin_name", nullable = false)
    private String foodOriginNm;

    /* 식품 대분류명 */
    @Column(name = "food_data_lv_3", nullable = false)
    private String foodLv3Nm;

    /* 대표 식품명 */
    @Column(name = "food_data_lv_4", nullable = false)
    private String foodLv4Nm;

    /* 식품 소분류명 */
    @Column(name = "food_data_lv_6", nullable = false)
    private String foodLv6Nm;

    /* 영양성분함량 기준량 */
    @Column(name = "food_data_qua", nullable = false)
    private long nutConSrtrQua;

    /* 칼로리 */
    @Column(name = "food_data_enerc", nullable = false)
    private double enerc;

    /* 단백질 */
    @Column(name = "food_data_prot", nullable = false)
    private double prot;

    /* 지방 */
    @Column(name = "food_data_fatce", nullable = false)
    private double fatce;

    /* 탄수화물 */
    @Column(name = "food_data_chocdf", nullable = false)
    private double chocdf;

    /* 당류 */
    @Column(name = "food_data_sugar", nullable = false)
    private double sugar;

    /* 나트륨 */
    @Column(name = "food_data_nat", nullable = false)
    private double nat;

    /* 콜레스테롤 */
    @Column(name = "food_data_chole", nullable = false)
    private double chole;

    @Column(name = "food_data_mfr_name")
    private String mfrNm;

    @Column(name = "food_data_rest_name")
    private String restNm;

    public static FoodApi fromDto(FoodApiDto dto) {
        FoodApi entity = new FoodApi();
        entity.foodCd = dto.foodCd();
        entity.foodNm = dto.foodNm();
        entity.dataCd = dto.dataCd();
        entity.typeNm = dto.typeNm();
        entity.foodOriginNm = dto.foodOriginNm();
        entity.foodLv3Nm = dto.foodLv3Nm();
        entity.foodLv4Nm = dto.foodLv4Nm();
        entity.foodLv6Nm = dto.foodLv6Nm();
        entity.nutConSrtrQua = parseQuantity(dto.nutConSrtrQua());
        entity.enerc = dto.enerc();
        entity.prot = dto.prot();
        entity.fatce = dto.fatce();
        entity.chocdf = dto.chocdf();
        entity.sugar = dto.sugar();
        entity.nat = dto.nat();
        entity.chole = dto.chole();
        entity.mfrNm = dto.mfrNm();
        entity.restNm = dto.restNm();

        return entity;
    }

    private static long parseQuantity(String value) {
        try {
            return Long.parseLong(value.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}

