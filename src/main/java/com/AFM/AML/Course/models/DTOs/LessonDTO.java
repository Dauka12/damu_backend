package com.AFM.AML.Course.models.DTOs;

public class LessonDTO {
    private int sub_chapter_id;
    private String sub_chapter_name;
    private boolean is_active;

    public int getSub_chapter_id() {
        return sub_chapter_id;
    }

    public void setSub_chapter_id(int sub_chapter_id) {
        this.sub_chapter_id = sub_chapter_id;
    }

    public String getSub_chapter_name() {
        return sub_chapter_name;
    }

    public void setSub_chapter_name(String sub_chapter_name) {
        this.sub_chapter_name = sub_chapter_name;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }
}
