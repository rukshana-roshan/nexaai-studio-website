package com.travelmate.controller;

import com.travelmate.model.ItineraryPlan;
import com.travelmate.model.ItineraryRequest;
import com.travelmate.service.ItineraryPlannerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/itinerary")
@CrossOrigin(origins = "*")
public class ItineraryController {

    private final ItineraryPlannerService itineraryPlannerService;

    @Autowired
    public ItineraryController(ItineraryPlannerService itineraryPlannerService) {
        this.itineraryPlannerService = itineraryPlannerService;
    }

    /**
     * FR-026 to FR-034: Generate optimized one-day itinerary.
     */
    @PostMapping("/plan")
    public ResponseEntity<?> generatePlan(@Valid @RequestBody ItineraryRequest request) {
        try {
            ItineraryPlan plan = itineraryPlannerService.generatePlan(request);
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error calculating itinerary: " + e.getMessage()));
        }
    }
}
