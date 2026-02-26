package tn.esprit.microservice.event.Repository;

import tn.esprit.microservice.event.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "events", path = "events")
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByTitleContaining(String title);

    List<Event> findByCategory(String category);

    @RestResource(path = "eventByTitle", rel = "eventByTitle")
    @Query("select e from Event e where e.title like concat('%', :title, '%')")
    List<Event> eventByTitle(@Param("title") String title);
}