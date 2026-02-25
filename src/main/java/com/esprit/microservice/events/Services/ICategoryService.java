package com.esprit.microservice.events.Services;

import com.esprit.microservice.events.Entities.Category;

import java.util.List;

public interface ICategoryService {

    Category addCategory(Category category);
    Category updateCategory(Long id,Category category);
    void deleteCategory(Long id);
    List<Category> getAllCategories();
    Category getCategoryById(Long id);



}
