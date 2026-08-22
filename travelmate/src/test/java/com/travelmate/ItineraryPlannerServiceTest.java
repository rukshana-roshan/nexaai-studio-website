package com.travelmate;

import com.travelmate.model.ItineraryPlan;
import com.travelmate.model.ItineraryRequest;
import com.travelmate.model.TouristAttraction;
import com.travelmate.repository.TouristAttractionRepository;
import com.travelmate.service.ItineraryPlannerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItineraryPlannerServiceTest {

    @Mock
    private TouristAttractionRepository attractionRepository;

    @InjectMocks
    private ItineraryPlannerService plannerService;

    private TouristAttraction attraction1;
    private TouristAttraction attraction2;
    private TouristAttraction attraction3;

    @BeforeEach
    void setUp() {
        attraction1 = new TouristAttraction(
                1L, "Pearl Bay, Bandaragama", "Recreation", "Water park", "img1.jpg",
                5.0, 3.0, "Bandaragama", 6.7205, 79.9880, "map1", "10am-4pm", "2500"
        );
        attraction2 = new TouristAttraction(
                2L, "Sri Lanka Karting Circuit", "Adventure", "Karting", "img2.jpg",
                4.0, 2.0, "Bandaragama", 6.7289, 79.9922, "map2", "2pm-8pm", "3000"
        );
        attraction3 = new TouristAttraction(
                3L, "Kalutara Bodhiya", "Religious / Cultural", "Temple", "img3.jpg",
                19.0, 1.5, "Kalutara", 6.5878, 79.9602, "map3", "6am-7pm", "Free"
        );
    }

    @Test
    void testGeneratePlan_WithinTimeBudget() {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(attractionRepository.findAllById(ids)).thenReturn(Arrays.asList(attraction1, attraction2));

        ItineraryRequest request = new ItineraryRequest();
        request.setAttractionIds(ids);
        request.setAvailableHours(8.0);
        request.setStartTime("08:30");
        request.setOptimizeRoute(true);

        ItineraryPlan plan = plannerService.generatePlan(request);

        assertNotNull(plan);
        assertEquals(2, plan.getStops().size());
        assertEquals("08:30 AM", plan.getStartTime());
        assertFalse(plan.isExceedingTime(), "Trip with 5.0h visit + travel should fit in 8.0h available");
        assertTrue(plan.getTotalVisitingHours() >= 5.0);
        assertNotNull(plan.getGoogleMapsDirectionsUrl());
    }

    @Test
    void testGeneratePlan_ExceedingTimeBudget() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(attractionRepository.findAllById(ids)).thenReturn(Arrays.asList(attraction1, attraction2, attraction3));

        ItineraryRequest request = new ItineraryRequest();
        request.setAttractionIds(ids);
        request.setAvailableHours(4.0); // Strict 4 hour budget
        request.setStartTime("09:00");
        request.setOptimizeRoute(true);

        ItineraryPlan plan = plannerService.generatePlan(request);

        assertNotNull(plan);
        assertEquals(3, plan.getStops().size());
        assertTrue(plan.isExceedingTime(), "Trip with 6.5h visit + travel should exceed 4.0h budget");
        assertTrue(plan.getTimeDifferenceHours() > 0);
        assertTrue(plan.getStatusMessage().contains("Time Warning"));
    }
}
