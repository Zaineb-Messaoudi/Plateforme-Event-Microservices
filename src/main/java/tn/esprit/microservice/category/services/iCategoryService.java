package tn.esprit.microservice.category.services;

import tn.esprit.microservice.category.entities.Category;

import java.util.List;

public interface iCategoryService {

    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    List<Category> searchCategoryByName(String name);
    List<Category> getActiveCategories();
    Category addCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(Long id);
}