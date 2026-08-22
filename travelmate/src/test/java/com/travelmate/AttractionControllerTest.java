package com.travelmate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelmate.controller.AttractionController;
import com.travelmate.dto.AttractionDTO;
import com.travelmate.model.TouristAttraction;
import com.travelmate.service.AttractionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttractionController.class)
public class AttractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttractionService attractionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllAttractions() throws Exception {
        TouristAttraction item = new TouristAttraction(
                1L, "Pearl Bay, Bandaragama", "Recreation", "Water park", "img1.jpg",
                5.0, 3.0, "Bandaragama", 6.7205, 79.9880, "map", "10am-4pm", "2500"
        );
        when(attractionService.searchAndFilter(null, null)).thenReturn(Arrays.asList(item));

        mockMvc.perform(get("/api/attractions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pearl Bay, Bandaragama"))
                .andExpect(jsonPath("$[0].category").value("Recreation"));
    }

    @Test
    void testGetAttractionById_Found() throws Exception {
        TouristAttraction item = new TouristAttraction(
                1L, "Pearl Bay, Bandaragama", "Recreation", "Water park", "img1.jpg",
                5.0, 3.0, "Bandaragama", 6.7205, 79.9880, "map", "10am-4pm", "2500"
        );
        when(attractionService.getAttractionById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/attractions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pearl Bay, Bandaragama"));
    }

    @Test
    void testCreateAttraction_Success() throws Exception {
        AttractionDTO dto = new AttractionDTO();
        dto.setName("Atulugama Eco Center");
        dto.setCategory("Nature / Scenic");
        dto.setDescription("Scenic nature spot in Atulugama");
        dto.setImage("https://example.com/image.jpg");
        dto.setDistance(1.5);
        dto.setVisitingDuration(1.0);
        dto.setLocation("Atulugama, Kalutara");

        TouristAttraction saved = new TouristAttraction(
                11L, dto.getName(), dto.getCategory(), dto.getDescription(), dto.getImage(),
                dto.getDistance(), dto.getVisitingDuration(), dto.getLocation(), null, null, null, null, null
        );

        when(attractionService.createAttraction(any(AttractionDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.name").value("Atulugama Eco Center"));
    }

    @Test
    void testCreateAttraction_ValidationFailure() throws Exception {
        AttractionDTO invalidDto = new AttractionDTO();
        invalidDto.setName(""); // Invalid blank name
        invalidDto.setDistance(-5.0); // Invalid negative distance

        mockMvc.perform(post("/api/attractions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
