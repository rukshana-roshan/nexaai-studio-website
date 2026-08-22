package com.travelmate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ItineraryRequest {

    @NotEmpty(message = "Please select at least one attraction for the itinerary")
    private List<Long> attractionIds;

    @NotNull(message = "Available hours is required")
    private Double availableHours = 8.0;

    private String startTime = "08:30";

    private Boolean optimizeRoute = true;

    private String startLocationName = "Atulugama, Kalutara";
    private Double startLatitude = 6.7167; // Atulugama / Bandaragama region coords
    private Double startLongitude = 80.0333;

    public ItineraryRequest() {
    }

    public List<Long> getAttractionIds() {
        return attractionIds;
    }

    public void setAttractionIds(List<Long> attractionIds) {
        this.attractionIds = attractionIds;
    }

    public Double getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(Double availableHours) {
        this.availableHours = availableHours;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Boolean getOptimizeRoute() {
        return optimizeRoute;
    }

    public void setOptimizeRoute(Boolean optimizeRoute) {
        this.optimizeRoute = optimizeRoute;
    }

    public String getStartLocationName() {
        return startLocationName;
    }

    public void setStartLocationName(String startLocationName) {
        this.startLocationName = startLocationName;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }
}
