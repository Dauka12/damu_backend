package com.AFM.AML.Course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Entity
@Table(name = "component_entry_values")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentEntryValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int component_entry_values_id;

    @ElementCollection
    @MapKeyColumn(name = "property_name")
    @Column(name = "property_value", columnDefinition = "TEXT")
    private Map<String, String> values;
}
