package com.travelmate.config;

import com.travelmate.model.TouristAttraction;
import com.travelmate.repository.TouristAttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TouristAttractionRepository repository;

    @Autowired
    public DataInitializer(TouristAttractionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            seedInitialAttractions();
        }
    }

    public void seedInitialAttractions() {
        repository.deleteAll();

        List<TouristAttraction> attractions = Arrays.asList(
            new TouristAttraction(
                null,
                "Pearl Bay, Bandaragama",
                "Recreation",
                "A premier international leisure and aquatic theme park in Bandaragama featuring 4 distinct adventure zones, exhilarating multi-lane speed water slides, splash pads, lazy river lagoons, and family dining facilities.",
                "https://images.unsplash.com/photo-1575429198097-0414ec08e8cd?w=1000&auto=format&fit=crop&q=80",
                5.0,
                3.0,
                "Bandaragama, Kalutara District",
                6.7205,
                79.9880,
                "https://www.google.com/maps/search/?api=1&query=6.7205,79.9880",
                "10:00 AM - 04:30 PM",
                "LKR 2,500 - 4,500"
            ),
            new TouristAttraction(
                null,
                "Sri Lanka Karting Circuit, Bandaragama",
                "Adventure",
                "The premier international karting and motorsport facility in South Asia with a 775-meter professional asphalt circuit, high-speed rental go-karts, safety gear, timing systems, and night racing floodlights.",
                "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=1000&auto=format&fit=crop&q=80",
                4.0,
                2.0,
                "Bandaragama, Kalutara District",
                6.7289,
                79.9922,
                "https://www.google.com/maps/search/?api=1&query=6.7289,79.9922",
                "02:00 PM - 08:00 PM",
                "LKR 3,000 / 10-min session"
            ),
            new TouristAttraction(
                null,
                "Bolgoda Lake",
                "Nature / Scenic",
                "The largest natural freshwater lake in the Western Province, offering tranquil eco-tours, bird watching, boating, paddle-boarding, and serene water vistas fringed by lush mangroves.",
                "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=1000&auto=format&fit=crop&q=80",
                6.5,
                2.0,
                "Bandaragama / Moratuwa border",
                6.7450,
                79.9480,
                "https://www.google.com/maps/search/?api=1&query=6.7450,79.9480",
                "07:00 AM - 10:00 AM or 04:00 PM - 06:30 PM",
                "Free (Boat hire ~LKR 3,500/hr)"
            ),
            new TouristAttraction(
                null,
                "Wadduwa Beach",
                "Beach",
                "A picturesque golden coastal shoreline renowned for its sweeping palm-lined stretch, calming Indian Ocean surf, vibrant beach volleyball, and scenic coastal photography.",
                "https://images.unsplash.com/photo-1506953823976-52e1fdc0149a?w=1000&auto=format&fit=crop&q=80",
                14.0,
                2.0,
                "Wadduwa, Kalutara District",
                6.6667,
                79.9333,
                "https://www.google.com/maps/search/?api=1&query=6.6667,79.9333",
                "04:30 PM - 06:45 PM",
                "Free admission"
            ),
            new TouristAttraction(
                null,
                "Pothupitiya Beach",
                "Beach",
                "A tranquil seaside haven located between Wadduwa and Kalutara, famous for uncrowded coastal strolls, traditional fishing catamaran scenery, and refreshing ocean breezes.",
                "https://images.unsplash.com/photo-1519046904884-53103b34b206?w=1000&auto=format&fit=crop&q=80",
                15.0,
                1.5,
                "Pothupitiya, Wadduwa",
                6.6380,
                79.9410,
                "https://www.google.com/maps/search/?api=1&query=6.6380,79.9410",
                "05:00 PM - 06:30 PM",
                "Free admission"
            ),
            new TouristAttraction(
                null,
                "Kalutara Bodhiya",
                "Religious / Cultural",
                "One of Sri Lanka's most revered sacred Buddhist pilgrimage sites, home to an ancient sacred Bo tree planted during the Anuradhapura era and the iconic giant white Chaitya stupa at the Kalutara Bridge over Kalu Ganga.",
                "https://images.unsplash.com/photo-1548013146-72479768bada?w=1000&auto=format&fit=crop&q=80",
                19.0,
                1.5,
                "Kalutara North, Kalutara District",
                6.5878,
                79.9602,
                "https://www.google.com/maps/search/?api=1&query=6.5878,79.9602",
                "06:00 AM - 09:00 AM or 05:00 PM - 07:30 PM",
                "Free admission"
            ),
            new TouristAttraction(
                null,
                "Gangatilaka Viharaya",
                "Religious / Cultural",
                "Renowned for having the only completely hollow Buddhist stupa in the world, featuring 74 vivid mural paintings depicting the 550 Jataka tales and magnificent terrace river views.",
                "https://images.unsplash.com/photo-1598890777032-bde835ba27c2?w=1000&auto=format&fit=crop&q=80",
                19.2,
                1.5,
                "Kalutara South, Kalutara District",
                6.5865,
                79.9610,
                "https://www.google.com/maps/search/?api=1&query=6.5865,79.9610",
                "08:00 AM - 11:30 AM",
                "Free admission"
            ),
            new TouristAttraction(
                null,
                "Richmond Castle",
                "Historical",
                "An opulent early 20th-century Edwardian mansion built between 1900 and 1910 across 42 acres, featuring teak woodwork from Burma, Italian stained glass, and historic European-style gardens.",
                "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=1000&auto=format&fit=crop&q=80",
                20.5,
                2.0,
                "Palatota, Kalutara",
                6.5702,
                79.9886,
                "https://www.google.com/maps/search/?api=1&query=6.5702,79.9886",
                "09:00 AM - 04:00 PM",
                "LKR 100 (Locals) / LKR 500 (Tourists)"
            ),
            new TouristAttraction(
                null,
                "Calido Beach",
                "Beach / Nature",
                "A rare and breathtaking coastal land spit situated directly between the flowing Kalu Ganga river and the Indian Ocean, perfect for sunset watching, sea breeze walks, and bird photography.",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1000&auto=format&fit=crop&q=80",
                21.0,
                1.5,
                "Kalutara",
                6.5815,
                79.9530,
                "https://www.google.com/maps/search/?api=1&query=6.5815,79.9530",
                "04:30 PM - 06:30 PM",
                "Free admission"
            ),
            new TouristAttraction(
                null,
                "Thudugala Ella",
                "Nature / Waterfall",
                "A refreshing 8-meter natural cascade nestled inside an old rubber and rainforest estate in Dodangoda, featuring cool natural plunge pools and lush canopy trails.",
                "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=1000&auto=format&fit=crop&q=80",
                22.5,
                2.5,
                "Thudugala, Dodangoda, Kalutara District",
                6.6025,
                80.0520,
                "https://www.google.com/maps/search/?api=1&query=6.6025,80.0520",
                "08:30 AM - 01:00 PM",
                "Free admission"
            )
        );

        repository.saveAll(attractions);
    }
}
