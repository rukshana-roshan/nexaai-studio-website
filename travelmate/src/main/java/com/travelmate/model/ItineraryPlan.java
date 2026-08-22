package com.travelmate.model;

import java.util.ArrayList;
import java.util.List;

public class ItineraryPlan {

    private String startLocation = "Atulugama, Kalutara District";
    private String startTime;
    private String estimatedEndTime;
    private Double availableHours;
    private Double totalVisitingHours;
    private Double totalTravelHours;
    private Double totalEstimatedHours;
    private Double totalTravelDistanceKm;
    private boolean isExceedingTime;
    private Double timeDifferenceHours;
    private String statusMessage;
    private int totalAttractionsCount;
    private List<ItineraryStop> stops = new ArrayList<>();
    private String googleMapsDirectionsUrl;
    private List<String> tipsAndSafetyNotes = new ArrayList<>();

    public ItineraryPlan() {
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEstimatedEndTime() {
        return estimatedEndTime;
    }

    public void setEstimatedEndTime(String estimatedEndTime) {
        this.estimatedEndTime = estimatedEndTime;
    }

    public Double getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(Double availableHours) {
        this.availableHours = availableHours;
    }

    public Double getTotalVisitingHours() {
        return totalVisitingHours;
    }

    public void setTotalVisitingHours(Double totalVisitingHours) {
        this.totalVisitingHours = totalVisitingHours;
    }

    public Double getTotalTravelHours() {
        return totalTravelHours;
    }

    public void setTotalTravelHours(Double totalTravelHours) {
        this.totalTravelHours = totalTravelHours;
    }

    public Double getTotalEstimatedHours() {
        return totalEstimatedHours;
    }

    public void setTotalEstimatedHours(Double totalEstimatedHours) {
        this.totalEstimatedHours = totalEstimatedHours;
    }

    public Double getTotalTravelDistanceKm() {
        return totalTravelDistanceKm;
    }

    public void setTotalTravelDistanceKm(Double totalTravelDistanceKm) {
        this.totalTravelDistanceKm = totalTravelDistanceKm;
    }

    public boolean isExceedingTime() {
        return isExceedingTime;
    }

    public void setExceedingTime(boolean exceedingTime) {
        isExceedingTime = exceedingTime;
    }

    public Double getTimeDifferenceHours() {
        return timeDifferenceHours;
    }

    public void setTimeDifferenceHours(Double timeDifferenceHours) {
        this.timeDifferenceHours = timeDifferenceHours;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getTotalAttractionsCount() {
        return totalAttractionsCount;
    }

    public void setTotalAttractionsCount(int totalAttractionsCount) {
        this.totalAttractionsCount = totalAttractionsCount;
    }

    public List<ItineraryStop> getStops() {
        return stops;
    }

    public void setStops(List<ItineraryStop> stops) {
        this.stops = stops;
    }

    public String getGoogleMapsDirectionsUrl() {
        return googleMapsDirectionsUrl;
    }

    public void setGoogleMapsDirectionsUrl(String googleMapsDirectionsUrl) {
        this.googleMapsDirectionsUrl = googleMapsDirectionsUrl;
    }

    public List<String> getTipsAndSafetyNotes() {
        return tipsAndSafetyNotes;
    }

    public void setTipsAndSafetyNotes(List<String> tipsAndSafetyNotes) {
        this.tipsAndSafetyNotes = tipsAndSafetyNotes;
    }
}
