package com.travelmate.service;

import com.travelmate.dto.AttractionDTO;
import com.travelmate.model.TouristAttraction;
import com.travelmate.repository.TouristAttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttractionService {

    private final TouristAttractionRepository attractionRepository;
    private final DataInitializer dataInitializer;

    @Autowired
    public AttractionService(TouristAttractionRepository attractionRepository, 
                             @org.springframework.context.annotation.Lazy DataInitializer dataInitializer) {
        this.attractionRepository = attractionRepository;
        this.dataInitializer = dataInitializer;
    }

    public void resetToDefaultSeedData() {
        dataInitializer.seedInitialAttractions();
    }

    public List<TouristAttraction> getAllAttractions() {
        return attractionRepository.findAllByOrderByDistanceAsc();
    }

    public Optional<TouristAttraction> getAttractionById(Long id) {
        return attractionRepository.findById(id);
    }

    public List<TouristAttraction> searchAndFilter(String category, String query) {
        String cleanCategory = (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("All")) 
                ? category.trim() : null;
        String cleanQuery = (query != null && !query.trim().isEmpty()) 
                ? query.trim() : null;

        if (cleanCategory == null && cleanQuery == null) {
            return attractionRepository.findAllByOrderByDistanceAsc();
        }

        return attractionRepository.filterAndSearch(cleanCategory, cleanQuery);
    }

    public List<String> getAllCategories() {
        return Arrays.asList(
                "All",
                "Recreation",
                "Adventure",
                "Nature / Scenic",
                "Beach",
                "Religious / Cultural",
                "Historical",
                "Nature / Waterfall"
        );
    }

    @Transactional
    public TouristAttraction createAttraction(AttractionDTO dto) {
        TouristAttraction attraction = new TouristAttraction();
        mapDtoToEntity(dto, attraction);
        return attractionRepository.save(attraction);
    }

    @Transactional
    public TouristAttraction updateAttraction(Long id, AttractionDTO dto) {
        TouristAttraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attraction with ID " + id + " not found"));
        mapDtoToEntity(dto, attraction);
        return attractionRepository.save(attraction);
    }

    @Transactional
    public void deleteAttraction(Long id) {
        if (!attractionRepository.existsById(id)) {
            throw new IllegalArgumentException("Attraction with ID " + id + " not found");
        }
        attractionRepository.deleteById(id);
    }

    private void mapDtoToEntity(AttractionDTO dto, TouristAttraction entity) {
        entity.setName(dto.getName());
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setImage(dto.getImage());
        entity.setDistance(dto.getDistance());
        entity.setVisitingDuration(dto.getVisitingDuration());
        entity.setLocation(dto.getLocation());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setMapLink(dto.getMapLink() != null && !dto.getMapLink().isBlank() ? dto.getMapLink() :
                generateGoogleMapsLink(dto.getName(), dto.getLocation(), dto.getLatitude(), dto.getLongitude()));
        entity.setBestTime(dto.getBestTime());
        entity.setEntryFee(dto.getEntryFee());
    }

    private String generateGoogleMapsLink(String name, String location, Double lat, Double lng) {
        if (lat != null && lng != null) {
            return "https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng;
        }
        return "https://www.google.com/maps/search/?api=1&query=" + 
                name.replace(" ", "+") + "+" + (location != null ? location.replace(" ", "+") : "Sri+Lanka");
    }
}
