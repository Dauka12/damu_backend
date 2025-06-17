package com.AFM.AML.Course.models.DTOs;

import java.util.List;
import java.util.Map;

public class ComponentEntryDTO {
    private String componentName;
    private List<Map<String, String>> inputs;
    private Map<String, String> values;


    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public List<Map<String, String>> getInputs() {
        return inputs;
    }

    public void setInputs(List<Map<String, String>> inputs) {
        this.inputs = inputs;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
