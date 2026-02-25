package tn.esprit.microservices.feedback;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FeedbackRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long eventId;

    @NotNull
    private Long organizerId;

    @NotNull
    @DecimalMin("1.0") @DecimalMax("5.0")
    private Double rating;

    @NotBlank
    @Size(min = 10, max = 1000)
    private String comment;
}
