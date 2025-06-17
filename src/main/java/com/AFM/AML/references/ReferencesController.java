package com.AFM.AML.references;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AFM.AML.User.models.Department;
import com.AFM.AML.User.models.Der;
import com.AFM.AML.User.models.dto.DerWithDepartmentsDTO;
import com.AFM.AML.references.service.ReferencesService;
import com.AFM.AML.statistics.repository.DepartmentRepository;
import com.AFM.AML.statistics.repository.DerRepository;

@RestController
@RequestMapping("/api/references")
public class ReferencesController {

    @Autowired
    private DerRepository derRepository;

    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private ReferencesService referencesService;

    @GetMapping("/ders")
    public List<Der> getAllDers() {
        // Возвращаем список всех ДЭР из таблицы der_list
        return derRepository.findAll();
    }

    @GetMapping("/departments")
    public List<Department> getAllDepartments() {
        // Возвращаем список всех департаментов из таблицы department
        return departmentRepository.findAll();
    }
    
    @GetMapping("/ders-with-departments")
    public List<DerWithDepartmentsDTO> getDersWithDepartments() {
        // Возвращаем список ДЭР с их департаментами
        return referencesService.getDersWithDepartments();
    }

}
