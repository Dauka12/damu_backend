package com.AFM.AML.Minio.controller;

import com.AFM.AML.Minio.service.MinioService;
import io.minio.ObjectStat;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class MinioMediaController {
    private final MinioService minioService;

    @GetMapping("/api/media/**")
    public ResponseEntity<InputStreamResource> getMedia(HttpServletRequest request) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String objectName = path.substring("/api/media/".length());

        ObjectStat stat = minioService.statObject(objectName);
        InputStream object = minioService.getObject(objectName);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (stat.contentType() != null && !stat.contentType().isBlank()) {
            mediaType = MediaType.parseMediaType(stat.contentType());
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(stat.length())
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(new InputStreamResource(object));
    }
}
