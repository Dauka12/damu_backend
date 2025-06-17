package com.AFM.AML.User.repository;

import com.AFM.AML.User.models.JobExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobExpRepo extends JpaRepository<JobExperience,Integer> {
    @Query(value = "select *  from job_experience where user_id = ?1",nativeQuery = true)
    List<JobExperience> findByUserId(int user_id);
}
