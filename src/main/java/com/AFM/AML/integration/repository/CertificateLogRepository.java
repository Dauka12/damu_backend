package com.AFM.AML.integration.repository;

import com.AFM.AML.integration.models.CertificateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CertificateLogRepository extends JpaRepository<CertificateLog, Long> {
    Optional<CertificateLog> findFirstByUserIdAndCourseIdAndStatus(Integer userId, Integer courseId, String status);
}
