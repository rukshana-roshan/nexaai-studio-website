package com.travelmate.service;

import com.travelmate.model.ItineraryPlan;
import com.travelmate.model.ItineraryRequest;
import com.travelmate.model.ItineraryStop;
import com.travelmate.model.TouristAttraction;
import com.travelmate.repository.TouristAttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItineraryPlannerService {

    private final TouristAttractionRepository attractionRepository;

    private static final double ATULUGAMA_LAT = 6.7167;
    private static final double ATULUGAMA_LNG = 80.0333;
    private static final String ATULUGAMA_LABEL = "Atulugama, Kalutara";

    @Autowired
    public ItineraryPlannerService(TouristAttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
    }

    public ItineraryPlan generatePlan(ItineraryRequest request) {
        if (request.getAttractionIds() == null || request.getAttractionIds().isEmpty()) {
            throw new IllegalArgumentException("No attractions provided for itinerary");
        }

        // Fetch attractions
        List<TouristAttraction> selectedAttractions = attractionRepository.findAllById(request.getAttractionIds());
        if (selectedAttractions.isEmpty()) {
            throw new IllegalArgumentException("Selected attractions not found");
        }

        // Maintain or optimize sequence
        List<TouristAttraction> orderedAttractions;
        if (Boolean.TRUE.equals(request.getOptimizeRoute())) {
            orderedAttractions = optimizeRoute(selectedAttractions, ATULUGAMA_LAT, ATULUGAMA_LNG);
        } else {
            // Preserve user-specified order
            Map<Long, TouristAttraction> map = selectedAttractions.stream()
                    .collect(Collectors.toMap(TouristAttraction::getId, a -> a));
            orderedAttractions = request.getAttractionIds().stream()
                    .filter(map::containsKey)
                    .map(map::get)
                    .collect(Collectors.toList());
        }

        ItineraryPlan plan = new ItineraryPlan();
        plan.setStartLocation(request.getStartLocationName() != null ? request.getStartLocationName() : ATULUGAMA_LABEL);
        plan.setAvailableHours(request.getAvailableHours() != null ? request.getAvailableHours() : 8.0);
        plan.setTotalAttractionsCount(orderedAttractions.size());

        // Parse starting time
        LocalTime currentTime;
        try {
            currentTime = LocalTime.parse(request.getStartTime() != null ? request.getStartTime() : "08:30");
        } catch (Exception e) {
            currentTime = LocalTime.of(8, 30);
        }
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        plan.setStartTime(currentTime.format(timeFormatter));

        double currentLat = request.getStartLatitude() != null ? request.getStartLatitude() : ATULUGAMA_LAT;
        double currentLng = request.getStartLongitude() != null ? request.getStartLongitude() : ATULUGAMA_LNG;
        String currentLocName = plan.getStartLocation();

        double totalVisitingHours = 0.0;
        int totalTravelMinutes = 0;
        double totalTravelDistanceKm = 0.0;

        List<ItineraryStop> stops = new ArrayList<>();
        List<String> googleMapsWaypoints = new ArrayList<>();
        googleMapsWaypoints.add("Atulugama, Sri Lanka");

        for (int i = 0; i < orderedAttractions.size(); i++) {
            TouristAttraction attraction = orderedAttractions.get(i);
            
            // Calculate distance & travel time from previous location
            double targetLat = attraction.getLatitude() != null ? attraction.getLatitude() : currentLat;
            double targetLng = attraction.getLongitude() != null ? attraction.getLongitude() : currentLng;
            
            double legDistanceKm = calculateDistanceKm(currentLat, currentLng, targetLat, targetLng);
            if (legDistanceKm <= 0.05 && attraction.getDistance() != null) {
                // If coordinates are identical or missing, use attraction distance offset
                legDistanceKm = Math.max(1.0, attraction.getDistance() * 0.4);
            }
            // Round to 1 decimal place
            legDistanceKm = Math.round(legDistanceKm * 10.0) / 10.0;
            totalTravelDistanceKm += legDistanceKm;

            int travelMinutes = estimateTravelMinutes(legDistanceKm);
            totalTravelMinutes += travelMinutes;

            // Advance time for travel
            currentTime = currentTime.plusMinutes(travelMinutes);
            String arrivalTimeStr = currentTime.format(timeFormatter);

            // Add visiting duration
            double durationHours = attraction.getVisitingDuration() != null ? attraction.getVisitingDuration() : 1.5;
            totalVisitingHours += durationHours;
            long visitMinutes = Math.round(durationHours * 60);

            // Advance time for visit
            currentTime = currentTime.plusMinutes(visitMinutes);
            String departureTimeStr = currentTime.format(timeFormatter);

            String travelNote = (i == 0) 
                    ? String.format("Depart %s -> Travel ~%s km (~%d mins) to %s", currentLocName, legDistanceKm, travelMinutes, attraction.getName())
                    : String.format("Drive ~%s km (~%d mins) from previous stop to %s", legDistanceKm, travelMinutes, attraction.getName());

            ItineraryStop stop = new ItineraryStop(
                    i + 1,
                    attraction,
                    arrivalTimeStr,
                    departureTimeStr,
                    durationHours,
                    travelMinutes,
                    legDistanceKm,
                    travelNote
            );
            stops.add(stop);

            // Prepare next leg coordinates
            currentLat = targetLat;
            currentLng = targetLng;
            currentLocName = attraction.getName();

            // Google Maps waypoints
            if (attraction.getLatitude() != null && attraction.getLongitude() != null) {
                googleMapsWaypoints.add(attraction.getLatitude() + "," + attraction.getLongitude());
            } else {
                googleMapsWaypoints.add(attraction.getName() + ", " + attraction.getLocation() + ", Sri Lanka");
            }
        }

        plan.setStops(stops);
        plan.setEstimatedEndTime(currentTime.format(timeFormatter));

        double totalTravelHours = Math.round((totalTravelMinutes / 60.0) * 10.0) / 10.0;
        double totalEstimatedHours = Math.round((totalVisitingHours + totalTravelHours) * 10.0) / 10.0;
        totalVisitingHours = Math.round(totalVisitingHours * 10.0) / 10.0;
        totalTravelDistanceKm = Math.round(totalTravelDistanceKm * 10.0) / 10.0;

        plan.setTotalVisitingHours(totalVisitingHours);
        plan.setTotalTravelHours(totalTravelHours);
        plan.setTotalEstimatedHours(totalEstimatedHours);
        plan.setTotalTravelDistanceKm(totalTravelDistanceKm);

        double availableHours = plan.getAvailableHours();
        boolean isExceeding = totalEstimatedHours > availableHours;
        plan.setExceedingTime(isExceeding);

        double diff = Math.round(Math.abs(totalEstimatedHours - availableHours) * 10.0) / 10.0;
        plan.setTimeDifferenceHours(diff);

        if (isExceeding) {
            plan.setStatusMessage(String.format(
                    "⚠️ Time Warning: Planned itinerary requires %.1f hours (%.1fh visit + %.1fh travel), which exceeds your available %.1fh budget by %.1f hours. Consider removing 1 or 2 stops.",
                    totalEstimatedHours, totalVisitingHours, totalTravelHours, availableHours, diff
            ));
        } else {
            plan.setStatusMessage(String.format(
                    "✅ Itinerary fits comfortably! Total time: %.1f hours (%.1fh visit + %.1fh travel). You have %.1f hours of buffer remaining in your day.",
                    totalEstimatedHours, totalVisitingHours, totalTravelHours, diff
            ));
        }

        // Generate multi-stop Google Maps URL
        plan.setGoogleMapsDirectionsUrl(buildGoogleMapsMultiStopUrl(googleMapsWaypoints));

        // Generate helpful safety & local travel tips
        plan.setTipsAndSafetyNotes(generateTips(orderedAttractions));

        return plan;
    }

    /**
     * Nearest Neighbor heuristic route optimization starting from Atulugama.
     */
    private List<TouristAttraction> optimizeRoute(List<TouristAttraction> attractions, double startLat, double startLng) {
        List<TouristAttraction> unvisited = new ArrayList<>(attractions);
        List<TouristAttraction> route = new ArrayList<>();

        double currentLat = startLat;
        double currentLng = startLng;

        while (!unvisited.isEmpty()) {
            TouristAttraction nearest = null;
            double minDistance = Double.MAX_VALUE;

            for (TouristAttraction a : unvisited) {
                double targetLat = a.getLatitude() != null ? a.getLatitude() : currentLat;
                double targetLng = a.getLongitude() != null ? a.getLongitude() : currentLng;
                double dist = calculateDistanceKm(currentLat, currentLng, targetLat, targetLng);
                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = a;
                }
            }

            if (nearest != null) {
                route.add(nearest);
                unvisited.remove(nearest);
                if (nearest.getLatitude() != null && nearest.getLongitude() != null) {
                    currentLat = nearest.getLatitude();
                    currentLng = nearest.getLongitude();
                }
            } else {
                route.addAll(unvisited);
                break;
            }
        }

        return route;
    }

    /**
     * Calculates distance between coordinates using Haversine formula.
     */
    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Estimates travel duration in minutes based on distance and average local traffic speed (~32 km/h + 5m parking/turn buffer).
     */
    private int estimateTravelMinutes(double distanceKm) {
        if (distanceKm <= 0.5) return 5;
        // Average speed ~30 km/h in suburban Kalutara/Bandaragama road networks + 5 mins buffer
        double hours = distanceKm / 30.0;
        int minutes = (int) Math.round(hours * 60) + 5;
        return Math.max(8, minutes);
    }

    private String buildGoogleMapsMultiStopUrl(List<String> waypoints) {
        if (waypoints == null || waypoints.isEmpty()) return "https://www.google.com/maps";
        
        StringBuilder sb = new StringBuilder("https://www.google.com/maps/dir/");
        for (String wp : waypoints) {
            sb.append(URLEncoder.encode(wp, StandardCharsets.UTF_8)).append("/");
        }
        return sb.toString();
    }

    private List<String> generateTips(List<TouristAttraction> attractions) {
        List<String> tips = new ArrayList<>();
        tips.add("📌 Note (NFR-006 / BR-007): Travel durations and distances are approximate. Please account for local traffic and weather conditions.");
        
        boolean hasReligious = attractions.stream().anyMatch(a -> a.getCategory().toLowerCase().contains("religious") || a.getName().toLowerCase().contains("bodhiya") || a.getName().toLowerCase().contains("viharaya"));
        if (hasReligious) {
            tips.add("🙏 Dress Code: Modest clothing covering shoulders and knees is required when visiting temples like Kalutara Bodhiya or Gangatilaka Viharaya.");
        }

        boolean hasBeach = attractions.stream().anyMatch(a -> a.getCategory().toLowerCase().contains("beach"));
        if (hasBeach) {
            tips.add("🌅 Beach Tip: Late afternoon is ideal for beach visits (Wadduwa, Pothupitiya, Calido) for comfortable temperatures and scenic sunset views.");
        }

        boolean hasWaterfall = attractions.stream().anyMatch(a -> a.getName().toLowerCase().contains("thudugala") || a.getCategory().toLowerCase().contains("waterfall"));
        if (hasWaterfall) {
            tips.add("👟 Waterfall Tip: Wear comfortable non-slip walking shoes when exploring Thudugala Ella and avoid visiting immediately after heavy monsoon rains.");
        }

        boolean hasAdventure = attractions.stream().anyMatch(a -> a.getCategory().toLowerCase().contains("adventure") || a.getCategory().toLowerCase().contains("recreation"));
        if (hasAdventure) {
            tips.add("🎟️ Recreation / Karting: Check open track sessions and advance slot availability for Pearl Bay and Sri Lanka Karting Circuit.");
        }

        return tips;
    }
}
