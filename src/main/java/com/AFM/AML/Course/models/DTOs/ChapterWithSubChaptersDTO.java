package com.AFM.AML.Course.models.DTOs;

import com.AFM.AML.Course.models.Chapter;
import com.AFM.AML.Course.models.Sub_chapter;

import java.util.List;

public class ChapterWithSubChaptersDTO {
    private Chapter chapter;
    private List<Sub_chapter> subChapters;

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public List<Sub_chapter> getSubChapters() {
        return subChapters;
    }

    public void setSubChapters(List<Sub_chapter> subChapters) {
        this.subChapters = subChapters;
    }
}
