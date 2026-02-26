package tn.esprit.microservice.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tn.esprit.microservice.event.Repository.EventRepository;
import tn.esprit.microservice.event.entities.Event;

import java.time.LocalDateTime;

@SpringBootApplication
public class EventApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class, args);
    }

    @Autowired
    private EventRepository repository;

    @Bean
    ApplicationRunner init() {
        return args -> {

            // Check if database is empty
            if (repository.count() == 0) {

                repository.save(new Event(null, "Tech Conference", "Big IT event",
                        "Technology", "Tunis",
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                        100, 50, true));

                repository.save(new Event(null, "Music Festival", "Live music event",
                        "Music", "Sousse",
                        LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                        300, 20, true));

                repository.save(new Event(null, "Startup Meetup", "Business networking",
                        "Business", "Sfax",
                        LocalDateTime.now(), LocalDateTime.now().plusHours(5),
                        80, 0, false));
            }

            // Display events in console
            repository.findAll().forEach(System.out::println);
        };
    }
}