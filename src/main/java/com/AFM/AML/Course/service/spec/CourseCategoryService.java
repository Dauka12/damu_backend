package com.AFM.AML.Course.service.spec;


import com.AFM.AML.Course.damu_dto.request.course_category.CourseCategoryCreateRequest;
import com.AFM.AML.Course.models.CourseCategory;
import com.AFM.AML.Course.models.CourseCategoryFiles;
import com.AFM.AML.exception.CourseCategoryAlreadyExistsException;

import java.util.List;
import java.util.Optional;

public interface CourseCategoryService {
    Optional<CourseCategory> getCourseCategoryById(int categoryId);
    void createCourseCategory(CourseCategoryCreateRequest courseCategoryCreateRequest) throws CourseCategoryAlreadyExistsException;
    void updateCourseCategory(CourseCategoryCreateRequest courseCategoryCreateRequest, int category_id);
    List<CourseCategory> getAllCourseCategories();
    void uploadCourseCategoryImage(int categoryId, List<String> image_url);
    void deleteCourseCategoryFile(int categoryFileId) throws Exception;
    void deleteCourseCategory(int categoryId) throws Exception;
    void saveAllFilesForCategory(List<CourseCategoryFiles> courseCategoryImages);
}
