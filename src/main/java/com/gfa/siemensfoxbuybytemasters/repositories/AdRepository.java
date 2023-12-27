package com.gfa.siemensfoxbuybytemasters.repositories;

import com.gfa.siemensfoxbuybytemasters.models.Ad;
import com.gfa.siemensfoxbuybytemasters.models.Category;
import com.gfa.siemensfoxbuybytemasters.models.DigitalProduct;
import com.gfa.siemensfoxbuybytemasters.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdRepository extends JpaRepository<Ad, UUID>, PagingAndSortingRepository<Ad, UUID> {
    long countAdsByCategory(Category category);
    List<Ad> findAllByUser(User user);
    List<Ad> findByUser(User user);
    Page<Ad> findByCategory_Id(long category_id, Pageable pageable);
    List<Ad> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
    @Query("SELECT d FROM DigitalProduct d WHERE d.id = :id")
    Optional<DigitalProduct> findDigitalProductById(@Param("id") UUID id);

}
