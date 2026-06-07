package com.example.webdulich.repository;

import com.example.webdulich.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    Optional<Property> findFirstByFeaturedTrueOrderByIdDesc();

    List<Property> findTop6ByOrderByIdDesc();

    List<Property> findTop3ByOrderByIdDesc();

    List<Property> findByAgentIdOrderByIdDesc(Long agentId);

    Optional<Property> findByModelMaTour(String modelMaTour);

    List<Property> findByModelMaTourIn(Collection<String> modelMaTours);

    @Query("""
        SELECT p
        FROM Property p
        WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:type IS NULL OR :type = '' OR LOWER(p.type) = LOWER(:type))
          AND (:location IS NULL OR :location = '' OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%')) OR LOWER(p.city) LIKE LOWER(CONCAT('%', :location, '%')))
          AND (:status IS NULL OR :status = '' OR LOWER(p.status) = LOWER(:status))
          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        ORDER BY p.id DESC
    """)
    List<Property> searchProperties(@Param("keyword") String keyword,
                                    @Param("type") String type,
                                    @Param("location") String location,
                                    @Param("status") String status,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice);

    long countByTypeIgnoreCase(String type);

    long countByCityIgnoreCase(String city);
}
