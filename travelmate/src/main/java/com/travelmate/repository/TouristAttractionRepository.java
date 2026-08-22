package com.travelmate.repository;

import com.travelmate.model.TouristAttraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TouristAttractionRepository extends JpaRepository<TouristAttraction, Long> {

    List<TouristAttraction> findByCategoryIgnoreCase(String category);

    @Query("SELECT a FROM TouristAttraction a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.location) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.category) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<TouristAttraction> searchAttractions(@Param("query") String query);

    @Query("SELECT a FROM TouristAttraction a WHERE " +
           "(:category IS NULL OR :category = '' OR LOWER(a.category) = LOWER(:category)) AND " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.location) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<TouristAttraction> filterAndSearch(@Param("category") String category, @Param("query") String query);

    List<TouristAttraction> findAllByOrderByDistanceAsc();
}
