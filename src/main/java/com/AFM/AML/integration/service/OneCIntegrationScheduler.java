package com.AFM.AML.integration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OneCIntegrationScheduler {

    private final OneCIntegrationService oneCIntegrationService;
    private final ConfigurableApplicationContext applicationContext;

    // Раз в сутки в 9:50 утра
    @Scheduled(cron = "0 10 10 * * *", zone = "Asia/Almaty")
    public void scheduleDailyExport() {
        // Проверка что контекст Spring ещё активен (не идёт shutdown)
        try {
            if (!applicationContext.isActive()) {
                System.out.println("Приложение завершается, пропускаем выгрузку в 1С");
                return;
            }
        } catch (Exception e) {
            System.out.println("Контекст недоступен, пропускаем выгрузку в 1С");
            return;
        }
        
        System.out.println("Запуск выгрузки данных в 1С...");
        oneCIntegrationService.sendFinishedToOneC();
    }
}
