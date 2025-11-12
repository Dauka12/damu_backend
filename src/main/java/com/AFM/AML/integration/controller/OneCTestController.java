package com.AFM.AML.integration.controller;

import com.AFM.AML.integration.service.OneCIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onec")
public class OneCTestController {

    @Autowired
    private OneCIntegrationService oneCIntegrationService;

    @PostMapping("/send-finished")
    public String sendAll() {
        oneCIntegrationService.sendFinishedToOneC();
        return "✅ Отправка завершена";
    }
}
