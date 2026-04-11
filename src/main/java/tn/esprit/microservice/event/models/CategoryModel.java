package tn.esprit.microservice.event.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {

    private Long id;
    private String name;
    private String description;
    private String color;
    private String icon;
    private boolean active;
}