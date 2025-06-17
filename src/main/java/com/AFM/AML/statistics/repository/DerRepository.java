package com.AFM.AML.statistics.repository;

import com.AFM.AML.User.models.Der;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DerRepository extends JpaRepository<Der, Integer> {
}
