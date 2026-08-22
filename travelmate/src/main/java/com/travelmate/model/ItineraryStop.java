package com.travelmate.model;

public class ItineraryStop {

    private int stopOrder;
    private TouristAttraction attraction;
    private String arrivalTime;
    private String departureTime;
    private Double visitingDurationHours;
    private Integer travelTimeFromPreviousMinutes;
    private Double distanceFromPreviousKm;
    private String travelNote;

    public ItineraryStop() {
    }

    public ItineraryStop(int stopOrder, TouristAttraction attraction, String arrivalTime,
                         String departureTime, Double visitingDurationHours,
                         Integer travelTimeFromPreviousMinutes, Double distanceFromPreviousKm,
                         String travelNote) {
        this.stopOrder = stopOrder;
        this.attraction = attraction;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.visitingDurationHours = visitingDurationHours;
        this.travelTimeFromPreviousMinutes = travelTimeFromPreviousMinutes;
        this.distanceFromPreviousKm = distanceFromPreviousKm;
        this.travelNote = travelNote;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public TouristAttraction getAttraction() {
        return attraction;
    }

    public void setAttraction(TouristAttraction attraction) {
        this.attraction = attraction;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Double getVisitingDurationHours() {
        return visitingDurationHours;
    }

    public void setVisitingDurationHours(Double visitingDurationHours) {
        this.visitingDurationHours = visitingDurationHours;
    }

    public Integer getTravelTimeFromPreviousMinutes() {
        return travelTimeFromPreviousMinutes;
    }

    public void setTravelTimeFromPreviousMinutes(Integer travelTimeFromPreviousMinutes) {
        this.travelTimeFromPreviousMinutes = travelTimeFromPreviousMinutes;
    }

    public Double getDistanceFromPreviousKm() {
        return distanceFromPreviousKm;
    }

    public void setDistanceFromPreviousKm(Double distanceFromPreviousKm) {
        this.distanceFromPreviousKm = distanceFromPreviousKm;
    }

    public String getTravelNote() {
        return travelNote;
    }

    public void setTravelNote(String travelNote) {
        this.travelNote = travelNote;
    }
}
