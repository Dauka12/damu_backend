package com.AFM.AML.User.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.AFM.AML.Course.models.DTOs.UserDTO;
import com.AFM.AML.User.models.User;
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByIin(String iin);
    boolean existsByEmail(String email);
    @Query("select new com.AFM.AML.Course.models.DTOs.UserDTO(u.id) from User u")
    List<UserDTO> getUsersDTO ();
    @Query(value = "select count(_user)>0 from _user where is_active = true and email = ?1",nativeQuery = true)
    boolean existsByEmailandIsActiveTrue(String email);
    User findByVerificationCode(String token);

//    void updateUserBy
}
