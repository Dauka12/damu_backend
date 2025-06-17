package com.AFM.AML.statistics.dto;

import java.util.List;

public class CourseStatisticsDto {
    private Long course_id;
    private String course_name;
    private int finished_count;
    private List<WhoFinishedCourseDto> who_finished;

    public CourseStatisticsDto() {
    }

    public CourseStatisticsDto(Long course_id,
                               String course_name,
                               int finished_count,
                               List<WhoFinishedCourseDto> who_finished) {
        this.course_id = course_id;
        this.course_name = course_name;
        this.finished_count = finished_count;
        this.who_finished = who_finished;
    }

    public Long getCourse_id() {
        return course_id;
    }

    public void setCourse_id(Long course_id) {
        this.course_id = course_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public int getFinished_count() {
        return finished_count;
    }

    public void setFinished_count(int finished_count) {
        this.finished_count = finished_count;
    }

    public List<WhoFinishedCourseDto> getWho_finished() {
        return who_finished;
    }

    public void setWho_finished(List<WhoFinishedCourseDto> who_finished) {
        this.who_finished = who_finished;
    }
}
