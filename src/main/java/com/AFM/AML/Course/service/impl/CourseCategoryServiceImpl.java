package com.AFM.AML.Course.service.impl;

import com.AFM.AML.Course.damu_dto.request.course_category.CourseCategoryCreateRequest;
import com.AFM.AML.Course.models.CourseCategory;
import com.AFM.AML.Course.models.CourseCategoryFiles;
import com.AFM.AML.Course.repository.spec.CourseCategoryFileRepository;
import com.AFM.AML.Course.repository.spec.CourseCategoryRepository;
import com.AFM.AML.Course.service.spec.CourseCategoryService;

import com.AFM.AML.User.exception.NotFoundException;
import com.AFM.AML.exception.CourseCategoryAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseCategoryServiceImpl implements CourseCategoryService {
    private final CourseCategoryRepository courseCategoryRepository;
    private final CourseCategoryFileRepository courseCategoryFileRepository;
    @Override
    public Optional<CourseCategory> getCourseCategoryById(int categoryId) {
        Optional<CourseCategory> courseCategory = courseCategoryRepository.findById(categoryId);
        if (courseCategory.isPresent() && !courseCategory.get().isDeleted()) {
            return courseCategory;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void createCourseCategory(CourseCategoryCreateRequest courseCategoryCreateRequest) throws CourseCategoryAlreadyExistsException {
        if (courseCategoryRepository.findByCategoryName(courseCategoryCreateRequest.getCategory_name()).isPresent()) {
            throw new CourseCategoryAlreadyExistsException("Category with name: " + courseCategoryCreateRequest.getCategory_name() + " already exists.");
        }
        CourseCategory courseCategory = new CourseCategory();
        courseCategory.setCategory_name(courseCategoryCreateRequest.getCategory_name());
        courseCategoryRepository.save(courseCategory);
    }

    @Override
    public void updateCourseCategory(CourseCategoryCreateRequest courseCategoryCreateRequest, int category_id) throws NotFoundException {
        Optional<CourseCategory> courseCategory = courseCategoryRepository.findById(category_id );
        if (courseCategory.isEmpty()) {
            throw new NotFoundException("Category with id: " + category_id + " not found.");
        }
        courseCategory.get().setCategory_name(courseCategoryCreateRequest.getCategory_name());
        courseCategoryRepository.save(courseCategory.get());
    }

    @Override
    public List<CourseCategory> getAllCourseCategories() {
        return courseCategoryRepository.findAllNonDeleted();
    }

    @Override
    public void uploadCourseCategoryImage(int categoryId, List<String> image_url) {

    }

    @Override
    public void deleteCourseCategoryFile(int categoryFileId) {
        Optional<CourseCategoryFiles> courseCategory = courseCategoryFileRepository.findById(categoryFileId);
        if (courseCategory.isPresent()) {
            CourseCategoryFiles categoryFile = courseCategory.get();
            categoryFile.setDeleted(true);
            courseCategoryFileRepository.save(categoryFile);
        } else {
            throw new NotFoundException("Category image with id: " + categoryFileId + " not found.");
        }
    }

    @Override
    public void deleteCourseCategory(int categoryId) {
        Optional<CourseCategory> courseCategory = courseCategoryRepository.findById(categoryId);
        if (courseCategory.isPresent()) {
            CourseCategory categoryFile = courseCategory.get();
            categoryFile.setDeleted(true);
            courseCategoryRepository.save(categoryFile);
        } else {
            throw new NotFoundException("Category with id: " + categoryId + " not found.");
        }
    }

    @Override
    public void saveAllFilesForCategory(List<CourseCategoryFiles> courseCategoryImages) {
        courseCategoryFileRepository.saveAll(courseCategoryImages);
    }
}
