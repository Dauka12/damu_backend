package com.AFM.AML.Course.models.game;

import com.AFM.AML.Course.models.Module;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer category_id;
    private String categoryName;
    private String categoryDescription;
    private String categoryImage;
    @Nullable
    private Boolean is_draft;
    @Nullable
    private Boolean is_deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private Game gameId;

    @OneToMany(mappedBy = "category")
    @OrderBy("level_id ASC")
    private List<Level> levels;


}
