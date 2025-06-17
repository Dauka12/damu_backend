package com.AFM.AML.statistics.dto;

import java.util.List;

public class TypeStatisticsDto {
    private String type_of_member;
    private int count_finished;
    private List<WhoFinishedDto> who_finished;

    public TypeStatisticsDto() {
    }

    public TypeStatisticsDto(String type_of_member, int count_finished, List<WhoFinishedDto> who_finished) {
        this.type_of_member = type_of_member;
        this.count_finished = count_finished;
        this.who_finished = who_finished;
    }

    public String getType_of_member() {
        return type_of_member;
    }

    public void setType_of_member(String type_of_member) {
        this.type_of_member = type_of_member;
    }

    public int getCount_finished() {
        return count_finished;
    }

    public void setCount_finished(int count_finished) {
        this.count_finished = count_finished;
    }

    public List<WhoFinishedDto> getWho_finished() {
        return who_finished;
    }

    public void setWho_finished(List<WhoFinishedDto> who_finished) {
        this.who_finished = who_finished;
    }
}
