package com.AFM.AML.Course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChapterCheckDTO {
    private int id;
    private Boolean checked;
    private int user_id;
    private int lesson_id;

    @Override
    public String toString() {
        return "UserChapterCheckDTO{" +
                "id=" + id +
                ", checked=" + checked +
                ", user_id=" + user_id +
                ", lesson_id=" + lesson_id +
                '}';
    }
}
