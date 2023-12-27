package com.gfa.siemensfoxbuybytemasters.repositories;

import com.gfa.siemensfoxbuybytemasters.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);

    Optional<Category> findById(long id);

    boolean existsById(long id);

    Optional<Category> findByName(String name);

}
