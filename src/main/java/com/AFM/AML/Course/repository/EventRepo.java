package com.AFM.AML.Course.repository;


import com.AFM.AML.Course.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Integer> {

    @Query(value = "Select * from event where ?1 < end_date ORDER BY start_date",nativeQuery = true)
    List<Event> findUpcomingEvents(Date date);

    @Query(value = "Select * from event where ?1 > end_date ORDER BY start_date DESC",nativeQuery = true)
    List<Event> findPassingEvents(Date date);
}
