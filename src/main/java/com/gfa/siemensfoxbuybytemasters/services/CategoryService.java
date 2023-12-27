package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.Category;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.CategoryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.*;

import org.springframework.validation.BindingResult;
import java.util.Map;
import java.util.Optional;

public interface CategoryService {
    Map<String, String> buildErrorJsonResponseforCategory(BindingResult bindingResult);

    Category createCategory(CategoryDTO categoryDTO);

    Category createCategory(Category category);

    Category createCategory(String name, String description, long id);

    boolean existsByName(String categoryName);

    Optional<Category> findCategoryById(long id);

    boolean existsById(long id);

    Category getDefaultCategory();

    ResponseEntity<?> deleteCategory(Long categoryId);
    List<Category> getAllCategories();

    long adCountByCategory(Category category);


    }
