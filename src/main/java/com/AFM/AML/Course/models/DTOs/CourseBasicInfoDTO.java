package com.AFM.AML.Course.models.DTOs;

import org.springframework.web.multipart.MultipartFile;

public class CourseBasicInfoDTO {
    private int id;
    private String title;
    private String audience;
    private String lang;
    private String category;
    private String price;
    private String image;
    private String language;

    public CourseBasicInfoDTO() {

    }
    public CourseBasicInfoDTO(int id, String title, String audience, String lang, String category, String price, String image) {
        this.id = id;
        this.title = title;
        this.audience = audience;
        this.lang = lang;
        this.category = category;
        this.price = price;
        this.language = language;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAudience() {
        return audience;
    }

    public String getLanguage() {
        return language;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

