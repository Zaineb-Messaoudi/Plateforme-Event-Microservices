package com.esprit.microservice.events.Controllers;

import com.esprit.microservice.events.Entities.Category;
import com.esprit.microservice.events.Services.ICategoryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@AllArgsConstructor

public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping("/addcategory")
    public Category addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    @PutMapping("/update/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/deletecategory/{idcategory}")

    public void deleteCategory(@PathVariable long idcategory) {
        categoryService.deleteCategory(idcategory);
    }

    @GetMapping("/findallcategory")
    public List<Category> findAllCategory() {
        return categoryService.getAllCategories();
    }
    @GetMapping("/findcategorybyid/{idcategory}")
    public Category findCategoryById(@PathVariable long idcategory) {
        return categoryService.getCategoryById(idcategory);
    }
}
