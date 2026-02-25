package tn.esprit.microservices.feedback;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FeedbackUpdateRequest {

    @DecimalMin("1.0") @DecimalMax("5.0")
    private Double rating;

    @Size(min = 10, max = 1000)
    private String comment;
}
