package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.UserWebinar;
import com.AFM.AML.Course.models.Webinar;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WebinarRepo extends JpaRepository<Webinar,Integer> {
    @Query(value = "select * from webinar", nativeQuery = true)
    List<Webinar> getWebinars();

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.webinar\n" +
            "    SET is_active = false\n" +
            "    WHERE \"date\" <= CURRENT_DATE - INTERVAL '1 day'", nativeQuery = true)
    void webinarUpdateIsActive();

    @Query(value = "SELECT u.*, us.user_id AS us_user_id, us.webinar_id AS us_webinar_id FROM webinar u INNER JOIN user_webinar us ON us.webinar_id = u.webinar_id WHERE us.user_id = ?1", nativeQuery = true)
    List<Webinar> getUserWebinarByUserId(int user_id);

    @Modifying
    @Transactional
    @Query(value = "Update webinar set image = ?1 where webinar_id = ?2",nativeQuery = true)
    void updateImage(String image, int webinar_id);

    @Query(value = "select * from webinar where is_active= true ORDER BY date ASC", nativeQuery = true)
    List<Webinar> getActiveWebinarsOrderedByDate();
}
