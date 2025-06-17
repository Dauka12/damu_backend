package com.AFM.AML.Course.controller;

import com.AFM.AML.Course.models.DTOs.WebinarArchiveDTO;
import com.AFM.AML.Course.models.WebinarArchive;
import com.AFM.AML.Course.service.WebinarArchiveService;
import com.AFM.AML.Course.utils.ImageUtil;
import com.AFM.AML.Minio.service.MinioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://192.168.122.132:3000", allowCredentials = "true")
@RequestMapping("/api/aml/webinar/archive")
@RequiredArgsConstructor
public class WebinarArchiveController {
    private final WebinarArchiveService webinarArchiveService;
    private final ImageUtil imageUtil;

    @GetMapping("/getWebinars")
    public ResponseEntity<?> getWebinars() {
        return ResponseEntity.ok(webinarArchiveService.getAllWebinarArchives());
    }

    @GetMapping("/getWebinar/{webinar_id}")
    public ResponseEntity<?> getWebinar(@PathVariable Integer webinar_id) {
        return ResponseEntity.ok(webinarArchiveService.getWebinarArchiveById(webinar_id));
    }

    @DeleteMapping("/deleteWebinar/{webinar_id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer webinar_id) {
        webinarArchiveService.deleteWebinarArchiveById(webinar_id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/createWebinar")
    public ResponseEntity<?> createWebinar(@RequestParam String data,@RequestParam MultipartFile file) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        WebinarArchiveDTO webinarArchiveDTO=mapper.readValue(data,WebinarArchiveDTO.class);
        webinarArchiveDTO.setWebinar_image(imageUtil.fileToMinio(file));
        WebinarArchive webinarArchive=new WebinarArchive(webinarArchiveDTO);
        return ResponseEntity.ok(webinarArchiveService.createWebinarArchive(webinarArchive));
    }

    @PutMapping("/updateWebinar/{webinar_id}")
    public ResponseEntity<?> updateWebinar(@RequestParam String data,
                                           @RequestParam MultipartFile file,@PathVariable Integer webinar_id) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        WebinarArchiveDTO webinarArchiveDTO=mapper.readValue(data,WebinarArchiveDTO.class);
        webinarArchiveDTO.setWebinar_image(imageUtil.fileToMinio(file));
        WebinarArchive webinarArchive=new WebinarArchive(webinarArchiveDTO);
        return ResponseEntity.ok(webinarArchiveService.updateWebinarArchive(webinarArchive,webinar_id));
    }
}
