package com.AFM.AML.Course.models.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subLevel_id;

    private String subLevel_name;

    @Nullable
    private Boolean is_draft;
    @Nullable
    private Boolean is_deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    @JsonIgnore
    private Level level;

    @OneToMany(mappedBy = "sublevel", cascade = CascadeType.ALL)
    private List<ComponentEntryGame> componentEntries;


}
