package com.project.growfit.global.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.growfit.global.api.dto.FoodApiDto;
import com.project.growfit.global.api.entity.FoodApi;
import com.project.growfit.global.api.repository.FoodApiRepository;
import com.project.growfit.global.api.service.OpenApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.retry.Retry;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OpenApiServiceImpl implements OpenApiService {
    private final FoodApiRepository foodDataRepository;
    private final WebClient webClient;
    private final String serviceKey;
    private final String numOfRows;
    private final String type;

    public OpenApiServiceImpl(
            FoodApiRepository foodDataRepository,
            WebClient foodWebClient,
            @Value("${api.service-key}") String serviceKey,
            @Value("${api.num-of-rows}") String numOfRows,
            @Value("${api.type}") String type
    ) {
        this.foodDataRepository = foodDataRepository;
        this.webClient = foodWebClient;
        this.serviceKey = serviceKey;
        this.numOfRows = numOfRows;
        this.type = type;
    }


    public void processDataToDb(){
        String apiUrl = "http://api.data.go.kr/openapi/tn_pubr_public_nutri_process_info_api";
        saveDataToDb(apiUrl);
    }

    public void foodDataToDb(){
        String apiUrl = "http://api.data.go.kr/openapi/tn_pubr_public_nutri_food_info_api";
        saveDataToDb(apiUrl);
    }

    @Transactional
    protected void saveDataToDb(String apiUrl) {
        log.info("데이터 저장 시작");
        int totalPages = 500;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int page = 1; page <= totalPages; page++) {
            futures.add(fetchAndSavePage(apiUrl, page));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("모든 페이지 저장 완료");
    }

    @Async
    protected CompletableFuture<Void> fetchAndSavePage(String apiUrl, int page) {
        try {
            String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
            URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("serviceKey", encodedKey)
                    .queryParam("pageNo", page)
                    .queryParam("numOfRows", numOfRows)
                    .queryParam("type", type)
                    .build(true)
                    .toUri();

            log.info("호출 page {} uri: {}", page, uri);

            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                    .toFuture()
                    .thenApply(response -> {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = null;
                        try {
                            root = mapper.readTree(response);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        JsonNode items = root.path("response").path("body").path("items");

                        if (!items.isArray() || items.isEmpty()) {
                            log.warn("page {}: 항목 없음", page);
                            return null;
                        }

                        List<FoodApi> dataList = new ArrayList<>();
                        for (JsonNode item : items) {
                            FoodApiDto dto = null;
                            try {
                                dto = mapper.treeToValue(item, FoodApiDto.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            FoodApi entity = FoodApi.fromDto(dto);
                            dataList.add(entity);
                        }

                        foodDataRepository.saveAll(dataList);
                        log.info(" page {} 저장 완료: {}건", page, dataList.size());

                        return null;
                    });

        } catch (Exception e) {
            log.error("page {} 처리 중 오류: {}", page, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }
}