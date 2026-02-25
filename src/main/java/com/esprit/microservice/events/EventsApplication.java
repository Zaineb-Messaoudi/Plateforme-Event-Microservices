package com.esprit.microservice.events;

import com.esprit.microservice.events.Entities.BlogPost;
import com.esprit.microservice.events.Entities.Category;
import com.esprit.microservice.events.Entities.Comment;
import com.esprit.microservice.events.Repositories.BlogPostRepository;
import com.esprit.microservice.events.Repositories.CategoryRepository;
import com.esprit.microservice.events.Repositories.CommentRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableDiscoveryClient

public class EventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventsApplication.class, args);
    }

    @Bean
    ApplicationRunner init(BlogPostRepository blogRepo, CategoryRepository catRepo, CommentRepository comRepo) {
        return args -> {
            if(catRepo.count() == 0) {
                Category cat1 = catRepo.save(new Category(null, "Tech", "Articles tech", null));
                Category cat2 = catRepo.save(new Category(null, "Lifestyle", "Articles lifestyle", null));

                BlogPost post1 = blogRepo.save(new BlogPost(null, "Java Tips", "Contenu Java...", "Nawres", LocalDateTime.now(), cat1, null));
                BlogPost post2 = blogRepo.save(new BlogPost(null, "Healthy Life", "Contenu santé...", "Sarra", LocalDateTime.now(), cat2, null));

                comRepo.save(new Comment(null, "Super article !", "Ali", LocalDateTime.now(), post1));
                comRepo.save(new Comment(null, "Merci pour les conseils", "Meriem", LocalDateTime.now(), post2));
            }
        };
    }


}
