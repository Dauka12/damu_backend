package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.UserWebinar;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserWebinarRepo extends JpaRepository<UserWebinar,Integer> {
    @Modifying
    @Transactional
    @Query(value = "update user_webinar set status = 'process' where user_id = ?1 and webinar_id = ?2",nativeQuery = true)
     void updateStatus(int user_id, int webinar_id);

    @Modifying
    @Transactional
    @Query(value = "delete from user_webinar where user_id = ?1 and webinar_id = ?2", nativeQuery = true)
    void deleteByUserAndWebinar(int user_id, int webinar_id);
//    @Query(value = "select * from user_webinar u inner join user_webinar us on  us.webinar_id = u.webinar_id   where user_id = ?1", nativeQuery = true)
//    List<UserWebinar> getUserWebinarByUserId(int user_id);
}
