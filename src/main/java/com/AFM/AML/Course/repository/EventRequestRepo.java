package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.EventRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRequestRepo extends JpaRepository<EventRequest, Integer> {

    List<EventRequest> findByEventId(int id);

    @NotNull List<EventRequest> findAll();
}
