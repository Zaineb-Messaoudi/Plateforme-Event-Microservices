package tn.esprit.microservice.event.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import tn.esprit.microservice.event.models.CategoryModel;

import java.util.List;

@FeignClient(name = "Category")
public interface CategoryClient {

    @GetMapping("/api/categories")
    List<CategoryModel> getAllCategories();

    @GetMapping("/api/categories/{id}")
    CategoryModel getCategoryById(@PathVariable Long id);

    @GetMapping("/api/categories/active")
    List<CategoryModel> getActiveCategories();
}