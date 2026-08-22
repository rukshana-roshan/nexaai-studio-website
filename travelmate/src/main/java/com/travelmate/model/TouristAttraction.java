package com.travelmate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing a tourist attraction around Atulugama.
 * Conforms to SRS Section 6.1 TOURIST_ATTRACTION specification.
 */
@Entity
@Table(name = "tourist_attraction")
public class TouristAttraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attraction_id")
    private Long id;

    @NotBlank(message = "Attraction name is required")
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Category is required")
    @Column(nullable = false, length = 80)
    private String category;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, length = 3000)
    private String description;

    @NotBlank(message = "Image URL is required")
    @Column(nullable = false, length = 1000)
    private String image;

    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.0", message = "Distance must be positive")
    @Column(nullable = false)
    private Double distance; // Distance in kilometers from Atulugama

    @NotNull(message = "Visiting duration is required")
    @DecimalMin(value = "0.1", message = "Visiting duration must be at least 0.1 hours")
    @Column(name = "visiting_duration", nullable = false)
    private Double visitingDuration; // Visiting duration in hours

    @NotBlank(message = "Location is required")
    @Column(nullable = false, length = 150)
    private String location; // Town/Area (e.g., Bandaragama, Kalutara)

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "map_link", length = 1000)
    private String mapLink;

    @Column(name = "best_time", length = 100)
    private String bestTime;

    @Column(name = "entry_fee", length = 100)
    private String entryFee;

    public TouristAttraction() {
    }

    public TouristAttraction(Long id, String name, String category, String description, String image,
                             Double distance, Double visitingDuration, String location,
                             Double latitude, Double longitude, String mapLink,
                             String bestTime, String entryFee) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.image = image;
        this.distance = distance;
        this.visitingDuration = visitingDuration;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mapLink = mapLink;
        this.bestTime = bestTime;
        this.entryFee = entryFee;
    }

    // Getters and Setters
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
