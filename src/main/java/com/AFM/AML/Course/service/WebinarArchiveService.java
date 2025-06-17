package com.AFM.AML.Course.service;

import com.AFM.AML.Course.models.WebinarArchive;
import com.AFM.AML.Course.repository.WebinarArchiveRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebinarArchiveService {
    @Autowired
    WebinarArchiveRepo webinarArchiveRepo;

    public WebinarArchive createWebinarArchive(WebinarArchive webinarArchive) {
        return webinarArchiveRepo.save(webinarArchive);
    }

    public List<WebinarArchive> getAllWebinarArchives() {
        return webinarArchiveRepo.findAll();
    }

    public Optional<WebinarArchive> getWebinarArchiveById(Integer id) {
        return webinarArchiveRepo.findById(id);
    }

    public Optional<WebinarArchive> updateWebinarArchive(WebinarArchive webinarArchive, Integer id) {
        return webinarArchiveRepo.findById(id)
                .map(existingWebinarArchive -> {
                    webinarArchive.setWebinarA_id(existingWebinarArchive.getWebinarA_id());
                    return webinarArchiveRepo.save(webinarArchive);
                });
    }

    public void deleteWebinarArchiveById(Integer id) {
        webinarArchiveRepo.deleteById(id);
    }
}
