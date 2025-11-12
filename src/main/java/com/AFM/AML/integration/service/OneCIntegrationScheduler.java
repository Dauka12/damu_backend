package com.AFM.AML.integration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OneCIntegrationScheduler {

    private final OneCIntegrationService oneCIntegrationService;

    // Раз в сутки в 3:00 ночи
    @Scheduled(fixedRate = 600000)    // Но прямо сейчас раз в час
    public void scheduleDailyExport() {
        System.out.println("Запуск выгрузки данных в 1С...");
        oneCIntegrationService.sendFinishedToOneC();
    }
}
