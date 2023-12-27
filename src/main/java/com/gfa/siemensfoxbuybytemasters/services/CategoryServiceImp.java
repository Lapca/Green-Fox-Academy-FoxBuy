package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.Ad;
import com.gfa.siemensfoxbuybytemasters.models.Category;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.CategoryDTO;
import com.gfa.siemensfoxbuybytemasters.repositories.AdRepository;
import com.gfa.siemensfoxbuybytemasters.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final AdRepository adRepository;
    private final LogService logService;

    @Autowired
    public CategoryServiceImp(CategoryRepository categoryRepository,
                              AdRepository adRepository, LogService logService) {

        this.categoryRepository = categoryRepository;
        this.adRepository = adRepository;
        this.logService = logService;
    }


    @Override
    public Map<String, String> buildErrorJsonResponseforCategory(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (fieldError.getField().equals("name")) {
                errors.put("error", "Category name field can not be empty!");
            }
        }
        return errors;
    }

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        return categoryRepository.save(
                new Category(categoryDTO.getName(),
                categoryDTO.getDescription()));
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category createCategory(String name, String description, long id) {
        return categoryRepository.save(new Category(name, description, id));
    }

    @Override
    public boolean existsByName(String categoryName){
        return categoryRepository.existsByName(categoryName);
    }

    @Override
    public Optional<Category> findCategoryById(long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public boolean existsById(long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public Category getDefaultCategory() {

        if (!existsByName("Uncategorized")) {
            return createCategory(new CategoryDTO("Uncategorized",
                    "Category for uncategorized ads."));
        }
        return categoryRepository.findByName("Uncategorized").get();

    }

    @Override
    public ResponseEntity<?> deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        Map<String, String> message = new HashMap<>();
        if (category != null) {
            Category defaultCategory = getDefaultCategory();

            List<Ad> adsToTransfer = category.getAds();
            for (Ad ad : adsToTransfer) {
                ad.setCategory(defaultCategory);
            }

            adRepository.saveAll(adsToTransfer);
            categoryRepository.delete(category);

            message.put("message", "Category has been deleted!");

            logService.logRequest("/category/{id}", true);
            return ResponseEntity.ok().body(message);
        }
        message.put("message", "Category doesn't exists!");
        logService.logRequest("/category/{id}", false);
        return ResponseEntity.badRequest().body(message);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public long adCountByCategory(Category category) {
        return adRepository.countAdsByCategory(category);
    }
}