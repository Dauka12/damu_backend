package com.AFM.AML.statistics.repository;

import com.AFM.AML.User.models.Department;
import com.AFM.AML.User.models.Der;
import com.AFM.AML.statistics.model.DerAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DerAccessRepository extends JpaRepository<DerAccess, Long> {

    // Находим запись по userId (предполагаем, что 1 userId = 1 запись)
    Optional<DerAccess> findByUserId(int userId);
}

