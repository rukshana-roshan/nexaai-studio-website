package com.travelmate.controller;

import com.travelmate.dto.AttractionDTO;
import com.travelmate.model.TouristAttraction;
import com.travelmate.service.AttractionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attractions")
@CrossOrigin(origins = "*")
public class AttractionController {

    private final AttractionService attractionService;

    @Autowired
    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    /**
     * FR-001, FR-011, FR-013: Retrieve all attractions with optional search and category filter.
     */
    @GetMapping
    public ResponseEntity<List<TouristAttraction>> getAttractions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query) {
        List<TouristAttraction> list = attractionService.searchAndFilter(category, query);
        return ResponseEntity.ok(list);
    }

    /**
     * FR-017 to FR-025: Get attraction details by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAttractionById(@PathVariable Long id) {
        return attractionService.getAttractionById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Attraction with ID " + id + " not found")));
    }

    /**
     * FR-014: Get all categories for filter options.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(attractionService.getAllCategories());
    }

    /**
     * FR-040, FR-044, FR-045: Admin Create attraction.
     */
    @PostMapping
    public ResponseEntity<?> createAttraction(@Valid @RequestBody AttractionDTO dto) {
        try {
            TouristAttraction created = attractionService.createAttraction(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Failed to create attraction: " + e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    /**
     * FR-042, FR-044, FR-045: Admin Update attraction.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAttraction(@PathVariable Long id, @Valid @RequestBody AttractionDTO dto) {
        try {
            TouristAttraction updated = attractionService.updateAttraction(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update: " + e.getMessage()));
        }
    }

    /**
     * FR-043, FR-046: Admin Delete attraction.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttraction(@PathVariable Long id) {
        try {
            attractionService.deleteAttraction(id);
            return ResponseEntity.ok(Map.of("message", "Attraction deleted successfully", "id", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete: " + e.getMessage()));
        }
    }

    /**
     * Reset attractions back to original 10 Atulugama tourist attractions.
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetAttractions() {
        try {
            attractionService.resetToDefaultSeedData();
            return ResponseEntity.ok(Map.of("message", "Successfully reset to 10 initial Atulugama attractions"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to reset: " + e.getMessage()));
        }
    }
}
