package com.AFM.AML.Course.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String ru_name;
    private String kz_name;
    private String ru_description;
    private String kz_description;
    private String location;
    private Date startDate;
    private Date endDate;
    private String coverImage;
    private String logoImage;
    private String typeOfStudy;
    private String formatOfStudy;

    //[{"time":"10:00","ru_name":"Начало мероприятия","kz_name":"Басталуы"},{"time":"18:00","ru_name":"Конец мероприятия","kz_name":"Бітуі"}]
    @Column(columnDefinition = "json")
    private String program;

    //[{"image":"url","name":"Ахметжан Дильназ Арыстанкызы","ru_position":"Разработчик ыявчаспмриочваспмриот енг","kz_position":"Басты программист"}]
    @Column(columnDefinition = "json")
    private String speakers;

    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventRequest> eventRequest;
}
