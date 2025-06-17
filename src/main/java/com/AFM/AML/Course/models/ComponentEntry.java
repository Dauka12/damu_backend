package com.AFM.AML.Course.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "component_entry")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int component_entry_id;
    private String componentName;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    @JsonIgnore
    private Lesson lesson;



    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "component_entry_values_id")
    private ComponentEntryValues values;
}
