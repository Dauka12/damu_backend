package com.AFM.AML.references.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AFM.AML.User.models.Department;
import com.AFM.AML.User.models.Der;
import com.AFM.AML.User.models.dto.DepartmentDTO;
import com.AFM.AML.User.models.dto.DerWithDepartmentsDTO;
import com.AFM.AML.statistics.repository.DepartmentRepository;
import com.AFM.AML.statistics.repository.DerRepository;

@Service
public class ReferencesService {

    @Autowired
    private DerRepository derRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<DerWithDepartmentsDTO> getDersWithDepartments() {
        List<Der> ders = derRepository.findAll();
        
        return ders.stream().map(der -> {
            // Получаем департаменты для данного ДЭР
            List<Department> departments = departmentRepository.findByDerId(der.getId());
            
            // Преобразуем департаменты в DTO
            List<DepartmentDTO> departmentDTOs = departments.stream()
                .map(dept -> new DepartmentDTO(dept.getId(), dept.getName_rus(), dept.getName_kaz()))
                .collect(Collectors.toList());
            
            // Создаем DTO для ДЭР с департаментами
            return new DerWithDepartmentsDTO(
                der.getId(),
                der.getName_rus(), 
                der.getName_kaz(),
                departmentDTOs
            );
        }).collect(Collectors.toList());
    }
}
