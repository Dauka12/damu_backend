package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.ComponentEntry;
import com.AFM.AML.Course.models.ComponentEntryValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentEntryValuesRepo extends JpaRepository<ComponentEntryValues, Integer> {
    @Query(value = "SELECT v1_0.*" +
            "FROM component_entry_values v1_0" +
            " INNER JOIN component_entry_values_values cevv ON v1_0.component_entry_values_id = cevv.component_entry_values_component_entry_values_id" +
            " WHERE cevv.property_name = 'img'", nativeQuery = true)
    List<ComponentEntryValues> getComponentEntriesByPhoto();

    @Query(value = "SELECT v1_0.*" +
            "FROM component_entry_values v1_0" +
            " INNER JOIN component_entry_values_values cevv ON v1_0.component_entry_values_id = cevv.component_entry_values_component_entry_values_id" +
            " WHERE cevv.property_value like 'http://192.168.122.132:9000/aml/%';", nativeQuery = true)
    List<ComponentEntryValues> getComponentEntriesByProperty();



}
