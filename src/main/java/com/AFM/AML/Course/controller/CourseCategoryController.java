package com.AFM.AML.Course.controller;

import com.AFM.AML.Course.damu_dto.request.course_category.CourseCategoryCreateRequest;
import com.AFM.AML.Course.damu_dto.request.course_category.CourseCategoryUploadFileRequest;
import com.AFM.AML.Course.models.CourseCategory;
import com.AFM.AML.Course.models.CourseCategoryFiles;
import com.AFM.AML.Course.service.spec.CourseCategoryService;
import com.AFM.AML.Minio.service.MinioService;
import com.AFM.AML.exception.CourseCategoryAlreadyExistsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Course Category Controller", description = "Endpoints for managing course categories")
@RestController
@RequestMapping("/api/aml/course-category")
@RequiredArgsConstructor
public class CourseCategoryController {
    private final MinioService minioService;
    private final CourseCategoryService courseCategoryService;
    @PostMapping("/create")
    @Operation(summary = "Create course category",
    responses = {
        @ApiResponse(responseCode = "200", description = "Course category created successfully"),
        @ApiResponse(responseCode = "400", description = "Course category already exists")
    })
    public ResponseEntity<?> createCourseCategory(@Valid @RequestBody CourseCategoryCreateRequest courseCategoryCreateRequest) throws CourseCategoryAlreadyExistsException {
        courseCategoryService.createCourseCategory(courseCategoryCreateRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/upload-files", consumes = "multipart/form-data")
    @Operation(summary = "Upload course category files",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
                    @ApiResponse(responseCode = "404", description = "Course category not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid image")
            })
    public ResponseEntity<?> uploadCourseCategoryFiles(
            @RequestParam("id") int id,
            @RequestParam("files") List<MultipartFile> files) throws Exception {

        CourseCategory courseCategory = courseCategoryService.getCourseCategoryById(id)
                .orElseThrow(() -> new RuntimeException("Course Category not found"));

        List<CourseCategoryFiles> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String filename = UUID.randomUUID().toString();
            minioService.uploadFile(file, filename);
            String fileUrl = minioService.getFileUrl(filename);
            CourseCategoryFiles courseCategoryFile = new CourseCategoryFiles();
            courseCategoryFile.setCourseCategory(courseCategory);
            courseCategoryFile.setFileUrl(fileUrl);
            courseCategoryFile.setFileName(file.getOriginalFilename());
            uploadedFiles.add(courseCategoryFile);
        }

        courseCategoryService.saveAllFilesForCategory(uploadedFiles);
        return ResponseEntity.ok("Files uploaded successfully");
    }

//    @DeleteMapping("/delete-image/{id}")
//    @Operation(summary = "Delete course category image",
//    responses = {
//        @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
//        @ApiResponse(responseCode = "404", description = "Course category not found")
//    })

    @GetMapping("/all")
    @Operation(summary = "All course categories",
    responses = {
        @ApiResponse(responseCode = "200", description = "All course categories retrieved")
    })
    public ResponseEntity<List<CourseCategory>> allCourseCategories(){
        List<CourseCategory> allCourseCategories = courseCategoryService.getAllCourseCategories();
        return ResponseEntity.ok(allCourseCategories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course category by id",
    responses = {
        @ApiResponse(responseCode = "200", description = "Course category retrieved"),
        @ApiResponse(responseCode = "404", description = "Course category not found")
    })
    public ResponseEntity<CourseCategory> getCourseCategoryById(@PathVariable int id) {
        CourseCategory courseCategory = courseCategoryService.getCourseCategoryById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course Category Not Found"));

        return ResponseEntity.ok(courseCategory);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Update course category",
    responses = {
        @ApiResponse(responseCode = "200", description = "Course category updated successfully"),
        @ApiResponse(responseCode = "404", description = "Course category not found")
    })
    public ResponseEntity<?> updateCourseCategory(@Valid @RequestBody CourseCategoryCreateRequest courseCategoryCreateRequest, @PathVariable int id){
        courseCategoryService.updateCourseCategory(courseCategoryCreateRequest, id);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete course category image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Course category deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Course category not found")
            })
    public ResponseEntity<?> deleteCourseCategory(@PathVariable int id){
        try {
            courseCategoryService.deleteCourseCategory(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-file/{id}")
    @Operation(summary = "Delete course category file",
    responses = {
        @ApiResponse(responseCode = "200", description = "Course category file deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Course category not found")
    })
    public ResponseEntity<?> deleteCourseCategoryFile(@PathVariable int id){
        try {
            courseCategoryService.deleteCourseCategoryFile(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
