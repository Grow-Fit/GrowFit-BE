package com.project.growfit.global.scheduler;

import com.project.growfit.global.api.service.OpenApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiScheduler {

    private final OpenApiService openApiService;

    // Test
    //@Scheduled(fixedRate = 1000000000)
    @Scheduled(cron = "0 0 0 1 * *")
    public void scheduleFoodDataUpdate() {
        log.info("===== 식품 데이터 업데이트 시작 =====");
        openApiService.foodDataToDb();
        log.info("===== 식품 데이터 업데이트 완료 =====");

        log.info("===== 가공식품 데이터 업데이트 시작 =====");
        openApiService.processDataToDb();
        log.info("===== 가공식품 데이터 업데이트 완료 =====");

    }
}
