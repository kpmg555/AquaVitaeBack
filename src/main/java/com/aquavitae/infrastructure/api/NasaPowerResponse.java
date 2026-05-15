package com.aquavitae.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class NasaPowerResponse {

    private String type;
    private Map<String, Object> properties;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }
    public Double getParameterValue(String parameterName) {
        if (properties == null) return null;
        Object paramObj = properties.get(parameterName);
        if (paramObj instanceof Map) {
            Map<String, Double> dateMap = (Map<String, Double>) paramObj;
            return dateMap.values().stream().findFirst().orElse(null);
        }
        return null;
    }
}