package com.AFM.AML.Course.controller;

import com.AFM.AML.Course.models.PostLink;
import com.AFM.AML.Course.models.Trash;
import com.AFM.AML.Course.repository.TrashRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@RestController
@RequestMapping("/api/trash")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@CrossOrigin(origins = "**.")
public class TrashController {
    @Autowired
    TrashRepository trashRepository;


    @PostMapping(value = "/add")
    public ResponseEntity<Object> addTrash(@RequestParam String iin) {
        System.out.println(iin);
        Trash object = new Trash();
        object.setTrash(iin);
        object.setDate(new Date()); // java.util.Date captures the current moment

        trashRepository.save(object);
        return ResponseEntity.ok(object);
    }
}
