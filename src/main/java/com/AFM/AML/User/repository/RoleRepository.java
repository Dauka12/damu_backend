package com.AFM.AML.User.repository;

import com.AFM.AML.User.models.DateDataDTO;
import com.AFM.AML.User.models.ERole;
import com.AFM.AML.User.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(ERole role);


}
