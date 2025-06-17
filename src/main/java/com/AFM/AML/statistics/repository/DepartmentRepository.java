package com.AFM.AML.statistics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.AFM.AML.User.models.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    
    List<Department> findByDerId(Integer derId);
    
    @Query("SELECT d FROM Department d WHERE d.derId = ?1")
    List<Department> findDepartmentsByDerId(Integer derId);
}
