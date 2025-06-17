package com.AFM.AML.Course.common;

public enum OrganizationCategory {
    GOVERNMENT_REGULATORY_BODIES("Государственные органы-регуляторы"),
    SFM("Субъект финансового мониторнга"),
    LAW_ENFORCEMENT_AGENCIES("Правоохранительные органы"),
    PUBLIC_ASSOCIATION("Общественное объединение");

    private final String category;

    OrganizationCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }
}
