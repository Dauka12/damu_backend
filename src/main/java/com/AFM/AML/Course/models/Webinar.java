package com.AFM.AML.Course.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Webinar {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int webinar_id;

    private String type;
    private String link;
    private Date date;
    @Column(columnDefinition = "text")
    private String image;
    private String minio_image;
    private String name;
    private String description;
    private Boolean is_active;
    private String webinar_for_member_of_the_system;

    @OneToMany(mappedBy = "webinar",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<UserWebinar> userWebinarSets = new HashSet<>();

    @Override
    public String toString() {
        return "Webinar{" +
                "webinar_id=" + webinar_id +
                ", type='" + type + '\'' +
                ", link='" + link + '\'' +
                ", date=" + date +
                ", image='" + image + '\'' +
                ", minio_image='" + minio_image + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", is_active=" + is_active +
                ", webinar_for_member_of_the_system='" + webinar_for_member_of_the_system + '\'' +
                '}';
    }
}
