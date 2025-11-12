package com.AFM.AML.integration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OneCIntegrationScheduler {

    private final OneCIntegrationService oneCIntegrationService;

    // Раз в сутки в 3:00 ночи
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Almaty")
    public void scheduleDailyExport() {
        System.out.println("Запуск выгрузки данных в 1С...");
        oneCIntegrationService.sendFinishedToOneC();
    }
}
