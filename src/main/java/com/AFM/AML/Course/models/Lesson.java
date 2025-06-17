package com.AFM.AML.Course.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "lesson")
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lesson_id;
    private String topic;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean is_active;

    @ManyToOne
    @JoinColumn(name = "module_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Module module;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<ComponentEntry> componentEntries;
    @OneToMany(mappedBy = "lesson",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserLessonCheck> userChapterChecks = new HashSet<>();

    @Override
    public String toString() {
        return "Lesson{" +
                "lesson_id=" + lesson_id +
                ", topic='" + topic + '\'' +
                ", is_active=" + is_active +
                ", module=" + module +
                ", componentEntries=" + componentEntries +
//                ", userChapterChecks=" + userChapterChecks +
                '}';
    }
}
