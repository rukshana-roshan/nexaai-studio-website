package com.travelmate.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AttractionDTO {

    private Long id;

    @NotBlank(message = "Attraction name cannot be blank")
    @Size(max = 150, message = "Attraction name cannot exceed 150 characters")
    private String name;

    @NotBlank(message = "Category is required")
    @Size(max = 80, message = "Category cannot exceed 80 characters")
    private String category;

    @NotBlank(message = "Description is required")
    @Size(max = 3000, message = "Description is too long")
    private String description;

    @NotBlank(message = "Image URL is required")
    private String image;

    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.0", message = "Distance must be 0 or greater")
    private Double distance;

    @NotNull(message = "Visiting duration is required")
    @DecimalMin(value = "0.1", message = "Visiting duration must be at least 0.1 hours (6 mins)")
    private Double visitingDuration;

    @NotBlank(message = "Location / Area is required")
    @Size(max = 150, message = "Location name is too long")
    private String location;

    private Double latitude;
    private Double longitude;
    private String mapLink;
    private String bestTime;
    private String entryFee;

    public AttractionDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getVisitingDuration() {
        return visitingDuration;
    }

    public void setVisitingDuration(Double visitingDuration) {
        this.visitingDuration = visitingDuration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getMapLink() {
        return mapLink;
    }

    public void setMapLink(String mapLink) {
        this.mapLink = mapLink;
    }

    public String getBestTime() {
        return bestTime;
    }

    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }

    public String getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(String entryFee) {
        this.entryFee = entryFee;
    }
}
