package tn.esprit.microservice.category.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice.category.entities.Category;
import tn.esprit.microservice.category.services.iCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final iCategoryService categoryService;

    // GET ALL CATEGORIES
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    // GET CATEGORY BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    // GET ACTIVE CATEGORIES
    @GetMapping("/active")
    public ResponseEntity<List<Category>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    // SEARCH CATEGORY BY NAME
    @GetMapping("/search")
    public ResponseEntity<List<Category>> searchCategory(
            @RequestParam(required = false) String name) {
        List<Category> categories = categoryService.searchCategoryByName(name);
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(categories);
    }

    // ADD CATEGORY
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        return new ResponseEntity<>(categoryService.addCategory(category),
                HttpStatus.CREATED);
    }

    // UPDATE CATEGORY
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id, @RequestBody Category category) {
        Category existing = categoryService.getCategoryById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setColor(category.getColor());
        existing.setIcon(category.getIcon());
        existing.setActive(category.isActive());
        return ResponseEntity.ok(categoryService.updateCategory(existing));
    }

    // DELETE CATEGORY
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        Category existing = categoryService.getCategoryById(id);
        if (existing == null) {
            return ResponseEntity.ok("Category not found");
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }
}