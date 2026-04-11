package tn.esprit.microservice.category;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import tn.esprit.microservice.category.entities.Category;
import tn.esprit.microservice.category.repository.CategoryRepository;

@SpringBootApplication
@EnableDiscoveryClient
public class CategoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CategoryApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
                categoryRepository.save(new Category(null, "Technology",
                        "Tech events and conferences", "#2196F3", "laptop", true));
                categoryRepository.save(new Category(null, "Music",
                        "Music festivals and concerts", "#E91E63", "music_note", true));
                categoryRepository.save(new Category(null, "Business",
                        "Business networking and meetups", "#4CAF50", "business", true));
                categoryRepository.save(new Category(null, "Sports",
                        "Sports events and competitions", "#FF9800", "sports", true));
                categoryRepository.save(new Category(null, "Art",
                        "Art exhibitions and workshops", "#9C27B0", "palette", false));

                System.out.println("✅ 5 categories initialized successfully!");
            }
        };
    }
}